package com.pnc.efg;

import com.pnc.efg.avro.schema.alerts.EfgAlert;
import com.pnc.efg.dto.AlertDto;
import com.pnc.efg.dto.PublishAlertRequest;
import com.pnc.efg.kafka.AvroReflectDeserializer;
import com.pnc.efg.service.EfgService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "efg-all-alerts" })
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "app.kafka.alert-topic=efg-all-alerts",
        "app.kafka.alert-reflect-class=com.pnc.efg.avro.schema.alerts.EfgAlert"
})
public class EfgAlertsEmbeddedKafkaTest {

    @Autowired
    private EfgService efgService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    public void publishAndConsumeAlert() throws Exception {
        AlertDto alert = new AlertDto();
        alert.setAlertId("ALERT-123");
        alert.setEventTime("2025-12-04T12:00:00Z");
        alert.setAlertType("TEST_ALERT");
        alert.setSeverity("MEDIUM");
        alert.setMessage("unit test alert");

        PublishAlertRequest req = new PublishAlertRequest();
        req.setPublishDateTime("2025-12-04T12:00:00Z");
        req.setSourceSystem("UNIT_TEST");
        req.setAlerts(Collections.singletonList(alert));

        // publish via service
        efgService.publishAlerts(req);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("alertsTestGroup", "true", embeddedKafka);
        consumerProps.put("avro.reflect.class", "com.pnc.efg.avro.schema.alerts.EfgAlert");

        Consumer<String, EfgAlert> consumer = new DefaultKafkaConsumerFactory<String, EfgAlert>(consumerProps,
                new StringDeserializer(), new AvroReflectDeserializer<EfgAlert>()).createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "efg-all-alerts");

        org.apache.kafka.clients.consumer.ConsumerRecords<String, EfgAlert> records = KafkaTestUtils.getRecords(consumer);
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        EfgAlert got = records.iterator().next().value();
        assertThat(got).isNotNull();
        assertThat(got.getSourceSystem()).isEqualTo("UNIT_TEST");
        assertThat(got.getAlertId()).isEqualTo("ALERT-123");

        consumer.close();
    }
}
