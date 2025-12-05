package com.pnc.efg.kafka;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EfgConsumer {

    private final Logger log = LoggerFactory.getLogger(EfgConsumer.class);

    @KafkaListener(topics = "${app.kafka.platform-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(EfgPlatformList payload) {
        log.info("Received EfgPlatformList message: {}", payload);
    }
}
