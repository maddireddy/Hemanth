package com.pnc.efg.kafka;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Example input
        List<Map<String, String>> inputList = new ArrayList<>();
        inputList.add(Map.of("ruleName", "Rule#1", "product", "Zelle-Send-Decline"));
        inputList.add(Map.of("ruleName", "Rule#2", "product", "SSW_Domestic-Send-Hold"));
        inputList.add(Map.of("ruleName", "Rule#3", "product", "Zelle-Send-Decline"));

        // Initialize controller
        AvroController controller = new AvroController("localhost:9092", "efg-platform-topic");

        // Process and publish consolidated Avro record
        controller.processAndPublish(inputList, "EFG-System");

        // Close producer
        controller.close();
    }
}
