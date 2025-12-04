package com.pnc.efg.config;

import com.pnc.efg.avro.schema.alerts.EfgAlert;
import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.kafka.AvroReflectSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.reflect-class}")
    private String reflectClass;

    @Value("${app.kafka.alert-reflect-class}")
    private String alertReflectClass;

    @Bean
    public ProducerFactory<String, EfgPlatformList> producerFactoryPlatform() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroReflectSerializer.class);
        props.put("avro.reflect.class", reflectClass);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EfgPlatformList> kafkaTemplatePlatform(ProducerFactory<String, EfgPlatformList> pf) {
        return new KafkaTemplate<>(pf);
    }

    @Bean
    public ProducerFactory<String, EfgAlert> producerFactoryAlert() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroReflectSerializer.class);
        props.put("avro.reflect.class", alertReflectClass);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, EfgAlert> kafkaTemplateAlert(ProducerFactory<String, EfgAlert> pf) {
        return new KafkaTemplate<>(pf);
    }
}
