oggerFactory.getLogger(AvroConverterController.class);

    private final KafkaTemplate<String, EfgPlatformList> kafkaTemplatePlatform;
    private final String platformTopic = "your-platform-topic-name"; // set via @Value if needed

    public AvroConverterController(KafkaTemplate<String, EfgPlatformList> kafkaTemplatePlatform) {
        this.kafkaTemplatePlatform = kafkaTemplatePlatform;
    }

    public String returnPlatformList(PublishRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("Received null or empty request for PlatformList.");
            return "Failure: Invalid request data.";
        }

        // --- 1. Flatten and group rules by product (deduplicated) ---
        Map<String, Set<String>> grouped = request.getItems().stream()
                .filter(Objects::nonNull)
                .flatMap(item -> {
                    String product = item.getProduct();
                    if (product == null) {
                        product = "UNKNOWN_PRODUCT"; // or skip this item
                    }

                    // Shape 1: item has List<String> rules
                    Stream<String> fromRulesList = Optional.ofNullable(item.getRules())
                            .orElse(Collections.emptyList())
                            .stream()
                            .filter(Objects::nonNull);

                    // Shape 2: item has single String ruleName
                    Stream<String> fromRuleName = Optional.ofNullable(item.getRuleName())
                            .filter(Objects::nonNull)
                            .stream();

                    return Stream.concat(fromRulesList, fromRuleName)
                            .map(rule -> Map.entry(product, rule));
                })
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toCollection(LinkedHashSet::new)
                        )
                ));

        // --- 2. Convert to Avro List<ProductRule> using Builder (Safe!) ---
        List<ProductRule> platformList = grouped.entrySet().stream()
                .map(entry -> ProductRule.newBuilder()
                        .setProduct(entry.getKey())
                        .setRules(new ArrayList<>(entry.getValue()))  // preserves insertion order
                        .build())
                .collect(Collectors.toList());

        // --- 3. Build final Avro message safely ---
        EfgPlatformList avroMessage = EfgPlatformList.newBuilder()
                .setPublishDateTime(request.getPublishDateTime())
                .setSourceSystem(request.getSourceSystem())
                .setPlatformList(platformList)
                .build();

        // --- 4. Send to Kafka asynchronously ---
        try {
            ListenableFuture<SendResult<String, EfgPlatformList>> future =
                    kafkaTemplatePlatform.send(platformTopic, null, avroMessage);

            future.addCallback(new ListenableFutureCallback<SendResult<String, EfgPlatformList>>() {
                @Override
                public void onSuccess(SendResult<String, EfgPlatformList> result) {
                    log.info("PlatformList successfully sent to Kafka topic: {} | Record: {}", 
                             platformTopic, avroMessage);
                }

                @Override
                public void onFailure(Throwable ex) {
                    log.error("Failed to send PlatformList to Kafka topic: {}", platformTopic, ex);
                }
            });

            return "Success: PlatformList processed and sent to Kafka topic '" + platformTopic + "'.";

        } catch (Exception e) {
            log.error("Exception during Kafka send for PlatformList", e);
            return "Failure: Exception occurred while publishing to Kafka - " + e.getMessage();
        }
    }
}