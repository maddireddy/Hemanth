package com.pnc.efg.kafka;

import com.pnc.efg.avro.schema.alerts.EfgAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EfgAlertConsumer {

    private final Logger log = LoggerFactory.getLogger(EfgAlertConsumer.class);

    @KafkaListener(topics = "${app.kafka.alert-topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(EfgAlert payload) {
        log.info("Received EfgAlert message: {}", payload);
    }
}
