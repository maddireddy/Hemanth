package com.pnc.efg;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.dto.ItemDto;
import com.pnc.efg.dto.PublishRequest;
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

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(partitions = 1, topics = { "efg-platform-list" })
@TestPropertySource(properties = {
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "app.kafka.platform-topic=efg-platform-list",
    "app.kafka.reflect-class=com.pnc.efg.avro.schema.platformList.EfgPlatformList"
})
public class EfgEmbeddedKafkaTest {

    @Autowired
    private EfgService efgService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Test
    public void publishAndConsumePlatformList() throws Exception {
        PublishRequest req = new PublishRequest();
        req.setPublishDateTime("2025-12-03T12:00:00Z");
        req.setSourceSystem("TEST");

        ItemDto item1 = new ItemDto();
        item1.setProduct("P1");
        item1.setRuleName("R1");

        ItemDto item2 = new ItemDto();
        item2.setProduct("P1");
        item2.setRuleName("R2");

        req.setItems(Arrays.asList(item1, item2));

        // publish via service (uses KafkaTemplate configured with embedded broker)
        efgService.publish(req);

        // create a consumer that uses the Avro reflect deserializer
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafka);
        consumerProps.put("avro.reflect.class", "com.pnc.efg.avro.schema.platformList.EfgPlatformList");

        Consumer<String, EfgPlatformList> consumer =
                new DefaultKafkaConsumerFactory<String, EfgPlatformList>(consumerProps,
                        new StringDeserializer(), new AvroReflectDeserializer<EfgPlatformList>()).createConsumer();

        embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "efg-platform-list");

        org.apache.kafka.clients.consumer.ConsumerRecords<String, EfgPlatformList> records = KafkaTestUtils.getRecords(consumer);
        assertThat(records.count()).isGreaterThanOrEqualTo(1);

        EfgPlatformList got = records.iterator().next().value();
        assertThat(got).isNotNull();
        assertThat(got.getSourceSystem()).isEqualTo("TEST");
        assertThat(got.getPlatformList()).isNotEmpty();

        consumer.close();
    }
}
