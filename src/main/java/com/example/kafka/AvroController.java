package com.example.kafka;

import com.example.kafka.model.ProductRule;
import com.example.kafka.model.ProductRulesEnvelope;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AvroController handles Kafka message publishing with Avro serialization.
 *
 * This controller reads input data, segregates rules by product, builds an Avro envelope,
 * and publishes to a configured Kafka topic using Confluent's KafkaAvroSerializer.
 *
 * Note: This requires a running Schema Registry instance (default: http://localhost:8081)
 */
public class AvroController {

    private final KafkaProducer<String, ProductRulesEnvelope> producer;
    private final String topic;

    /**
     * Constructs an AvroController with the specified Kafka bootstrap servers and topic.
     *
     * @param bootstrapServers Kafka bootstrap servers (e.g., "localhost:9092")
     * @param topic Kafka topic name to publish messages to
     */
    public AvroController(String bootstrapServers, String topic) {
        this(bootstrapServers, topic, "http://localhost:8081");
    }

    /**
     * Constructs an AvroController with custom Schema Registry URL.
     *
     * @param bootstrapServers Kafka bootstrap servers
     * @param topic Kafka topic name
     * @param schemaRegistryUrl Schema Registry URL
     */
    public AvroController(String bootstrapServers, String topic, String schemaRegistryUrl) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", schemaRegistryUrl);
        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    /**
     * Reads input, segregates rules by product, builds one Avro envelope, and publishes to Kafka.
     *
     * Expected input format: List of maps containing "product" and "ruleName" keys.
     * Example:
     * [
     *   {"product": "Zelle-Send-Decline", "ruleName": "Rule#1"},
     *   {"product": "SSW_Domestic-Send-Hold", "ruleName": "Rule#2"}
     * ]
     *
     * @param inputList List of input data containing product and rule information
     */
    public void processAndPublish(List<Map<String, String>> inputList) {
        // Group rules by product
        Map<String, List<String>> productRules = inputList.stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.get("product"),
                        Collectors.mapping(entry -> entry.get("ruleName"), Collectors.toList())
                ));

        // Build ProductRule list
        List<ProductRule> productRuleList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : productRules.entrySet()) {
            ProductRule pr = new ProductRule(entry.getKey(), entry.getValue());
            productRuleList.add(pr);
        }

        // Wrap in envelope
        ProductRulesEnvelope envelope = new ProductRulesEnvelope(productRuleList);

        // Publish single message
        producer.send(new ProducerRecord<>(topic, "all-products", envelope));
        System.out.println("Published consolidated Avro record: " + envelope);
    }

    /**
     * Closes the Kafka producer and releases resources.
     */
    public void close() {
        producer.close();
    }
}
