package com.pnc.efg.kafka;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.avro.schema.platformList.ProductRule;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AvroController {

    private final KafkaProducer<String, EfgPlatformList> producer;
    private final String topic;

    public AvroController(String bootstrapServers, String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081"); // adjust if needed
        this.producer = new KafkaProducer<>(props);
        this.topic = topic;
    }

    /**
     * Reads input, segregates rules by product, builds one EfgPlatformList record, and publishes to Kafka.
     */
    public String processAndPublish(List<ProductRule> inputList) {
    // Group rules by product
    Map<String, List<String>> productRules = inputList.stream()
            .collect(Collectors.groupingBy(
                    ProductRule::getProduct,
                    Collectors.mapping(ProductRule::getRuleName, Collectors.toList())
            ));

    // Build ProductRule list
    List<ProductRule> productRuleList = new ArrayList<>();
    for (Map.Entry<String, List<String>> entry : productRules.entrySet()) {
        ProductRule pr = ProductRule.newBuilder()
                .setProduct(entry.getKey())
                .setRules(entry.getValue())
                .build();
        productRuleList.add(pr);
    }

    // Build EfgPlatformList record
    EfgPlatformList efgRecord = EfgPlatformList.newBuilder()
            .setPublishDateTime(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
            .setSourceSystem("") // Removed sourceSystem parameter
            .setPlatformList(productRuleList)
            .build();

    // Publish single message
    producer.send(new ProducerRecord<>(topic, "platform-rules", efgRecord));
    System.out.println("Published platform rules");

    return efgRecord.toString();
    }

    public void close() {
        producer.close();
    }
}
