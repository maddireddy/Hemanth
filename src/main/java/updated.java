public void processAndPublish(String rawInput, String sourceSystem) {

    // ✅ Step 1: Normalize and clean the raw input string
    String cleaned = rawInput
            .trim()
            .replaceAll("^\\[|\\]$", "")     // remove outer [ ]
            .replaceAll("\\s+", " ")         // normalize whitespace
            .replaceAll("\\{ ", "{")         // cleanup formatting
            .replaceAll(" }", "}");

    // ✅ Step 2: Split into individual object strings
    String[] items = cleaned.split("},\\s*\\{");

    List<Map<String, String>> inputList = new ArrayList<>();

    for (String item : items) {
        item = item.replace("{", "").replace("}", "").trim();

        Map<String, String> map = new HashMap<>();
        String[] pairs = item.split(",");

        for (String pair : pairs) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                map.put(key, value);
            }
        }
        inputList.add(map);
    }

    // ✅ Step 3: Group rules by product
    Map<String, List<String>> productRules = inputList.stream()
            .collect(Collectors.groupingBy(
                    entry -> entry.get("product"),
                    Collectors.mapping(entry -> entry.get("ruleName"), Collectors.toList())
            ));

    // ✅ Step 4: Build ProductRule list
    List<ProductRule> productRuleList = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : productRules.entrySet()) {
        ProductRule pr = ProductRule.newBuilder()
                .setProduct(entry.getKey())
                .setRules(entry.getValue())
                .build();
        productRuleList.add(pr);
    }

    // ✅ Step 5: Build EfgPlatformList Avro record
    EfgPlatformList efgRecord = EfgPlatformList.newBuilder()
            .setPublishDateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
            .setSourceSystem(sourceSystem)
            .setPlatformList(productRuleList)
            .build();

    // ✅ Step 6: Publish to Kafka with callback logging
    producer.send(new ProducerRecord<>(topic, "platform-rules", efgRecord), (metadata, exception) -> {
        if (exception != null) {
            exception.printStackTrace();
            System.err.println("Kafka send failed: " + exception.getMessage());
        } else {
            System.out.println("Kafka send succeeded: " + metadata.toString());
        }
    });
}
