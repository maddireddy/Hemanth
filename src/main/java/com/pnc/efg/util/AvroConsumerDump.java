package com.pnc.efg.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.kafka.AvroReflectDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * Small standalone consumer utility to decode Avro-reflect messages using the project's
 * AvroReflectDeserializer and print them as JSON. Usage:
 *   java -cp build/libs/*-SNAPSHOT.jar com.pnc.efg.util.AvroConsumerDump <topic> [bootstrapServer] [maxMessages]
 */
public class AvroConsumerDump {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: AvroConsumerDump <topic> [bootstrapServer] [maxMessages]");
            System.exit(2);
        }

        String topic = args[0];
        String bootstrap = args.length > 1 ? args[1] : "localhost:9092";
        int maxMessages = args.length > 2 ? Integer.parseInt(args[2]) : 5;

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroReflectDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro-dump-" + UUID.randomUUID().toString());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put("avro.reflect.class", EfgPlatformList.class.getName());

        KafkaConsumer<String, EfgPlatformList> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));

        ObjectMapper mapper = new ObjectMapper();
        int received = 0;
        System.out.println("Subscribed to topic: " + topic + " on " + bootstrap + ", waiting for up to " + maxMessages + " messages...");

        try {
            while (received < maxMessages) {
                ConsumerRecords<String, EfgPlatformList> records = consumer.poll(Duration.ofSeconds(5));
                if (records.isEmpty()) continue;
                records.forEach(r -> {
                    try {
                        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(r.value());
                        System.out.println("--- message (partition=" + r.partition() + ", offset=" + r.offset() + ") ---");
                        System.out.println(json);
                        System.out.println();
                    } catch (Exception e) {
                        System.err.println("Failed to print message: " + e.getMessage());
                    }
                });
                received += records.count();
            }
        } finally {
            consumer.close();
        }
    }
}
