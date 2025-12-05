**
     * Groups incoming items by product and builds an EfgPlatformList POJO, then sends to Kafka.
     * This method is intended to be registered in your Actimize plugin configuration.
     */
    public String returnPlatformList(PublishRequest request) {
        if (request == null || request.getItems() == null || request.getItems().isEmpty()) {
            log.warn("Received null or empty request for PlatformList.");
            return "Failure: Invalid request data.";
        }

        // --- 1. Flatten and Segregate Logic (as requested) ---
        // Groups by product -> unique set of rules
        Map<String, Set<String>> grouped = request.getItems().stream()
                .filter(Objects::nonNull)
                .flatMap(item -> {
                    String product = item.getProduct();
                    
                    // Handle Shape 1: items[].rules -> list of rules
                    List<String> rulesList = item.getRules() != null ? item.getRules() : Collections.emptyList();
                    Stream<Map.Entry<String, String>> fromList = rulesList.stream()
                        .filter(Objects::nonNull) // Ensure rules are not null
                        .map(r -> new AbstractMap.SimpleEntry<>(product, r));
                    
                    // Handle Shape 2: items[].ruleName -> single rule per item
                    Stream<Map.Entry<String, String>> fromRuleName = Stream.empty();
                    if (item.getRuleName() != null) {
                        fromRuleName = Stream.of(new AbstractMap.SimpleEntry<>(product, item.getRuleName()));
                    }
                    
                    return Stream.concat(fromList, fromRuleName);
                })
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toCollection(LinkedHashSet::new))));

        // Convert grouped Map into List<ProductRule> Avro structure
        List<ProductRule> platformList = grouped.entrySet().stream()
                .map(e -> new ProductRule(e.getKey(), new ArrayList<>(e.getValue())))
                .collect(Collectors.toList());

        // --- 2. Build the Avro Record (EfgPlatformList) ---
        EfgPlatformList out = new EfgPlatformList();
        out.setPublishDateTime(request.getPublishDateTime());
        out.setSourceSystem(request.getSourceSystem());
        out.setPlatformList(platformList);

        // --- 3. Send to Kafka (Kafkaesque Publishing) ---
        try {
            // Note: Sending without a key (key is null)
            ListenableFuture<SendResult<String, EfgPlatformList>> future = kafkaTemplatePlatform.send(platformTopic, null, out);
            
            future.addCallback(new ListenableFutureCallback<SendResult<String, EfgPlatformList>>() {
                @Override
                public void onSuccess(SendResult<String, EfgPlatformList> result) {
                    log.info("PlatformList message sent to topic {}: {}", platformTopic, out);
                }

                @Override
                public void onFailure(Throwable ex) {
                    // Log the error but continue gracefully (per your requirement)
                    log.error("Failed to send PlatformList message to topic {}: {}", platformTopic, ex.getMessage(), ex);
                }
            });
            return "Success: PlatformList request processed and message published to Kafka.";
        } catch (Exception e) {
            log.error("Exception while sending PlatformList message to Kafka: {}", e.getMessage(), e);
            // Return failure status
            return "Failure: Exception occurred during Kafka send.";
        }
    }
