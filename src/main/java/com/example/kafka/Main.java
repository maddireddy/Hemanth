package com.example.kafka;

import java.util.*;

/**
 * Main class demonstrating the usage of AvroController for publishing
 * product rules to Kafka using Avro serialization.
 *
 * Prerequisites:
 * - Kafka broker running (default: localhost:9092)
 * - Schema Registry running (default: http://localhost:8081)
 *
 * You can start these services using the provided docker-compose.yml in the project root.
 */
public class Main {
    public static void main(String[] args) {
        // Example input - simulates data from a database or external source
        List<Map<String, String>> inputList = new ArrayList<>();
        inputList.add(Map.of("ruleName", "Rule#1", "product", "Zelle-Send-Decline"));
        inputList.add(Map.of("ruleName", "Rule#2", "product", "SSW_Domestic-Send-Hold"));
        inputList.add(Map.of("ruleName", "Rule#3", "product", "Zelle-Send-Decline"));

        // Initialize controller with Kafka configuration
        // Adjust bootstrap servers and topic as needed for your environment
        AvroController controller = new AvroController(
            "localhost:9092",           // Kafka bootstrap servers
            "product-rules-topic"       // Topic name
        );

        // Process and publish consolidated Avro record
        // This will group rules by product and send a single message containing all products
        controller.processAndPublish(inputList);

        // Clean up resources
        controller.close();

        System.out.println("Processing complete. Check Kafka topic 'product-rules-topic' for the message.");
    }
}
