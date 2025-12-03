package com.pnc.efg.service;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.avro.schema.platformList.ProductRule;
import com.pnc.efg.dto.ItemDto;
import com.pnc.efg.dto.PublishRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EfgService {
    private static final Logger log = LoggerFactory.getLogger(EfgService.class);

    private final KafkaTemplate<String, EfgPlatformList> kafkaTemplate;
    private final String topic;

    public EfgService(KafkaTemplate<String, EfgPlatformList> kafkaTemplate,
                      @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    /**
     * Groups incoming items by product and builds an EfgPlatformList POJO, then sends to Kafka.
     */
    public void publish(PublishRequest request) {
        if (request == null || request.getItems() == null) return;

        // Flatten and group by product -> unique rules set
        // Support two input shapes:
        // 1) items[].rules -> list of rules
        // 2) items[].ruleName -> single rule per item
        Map<String, Set<String>> grouped = request.getItems().stream()
                .filter(Objects::nonNull)
                .flatMap(item -> {
                    String product = item.getProduct();
                    List<String> rulesList = item.getRules() != null ? item.getRules() : Collections.emptyList();
                    Stream<Map.Entry<String, String>> fromList = rulesList.stream().map(r -> new AbstractMap.SimpleEntry<>(product, r));
                    Stream<Map.Entry<String, String>> fromRuleName = Stream.empty();
                    if (item.getRuleName() != null) {
                        fromRuleName = Stream.of(new AbstractMap.SimpleEntry<>(product, item.getRuleName()));
                    }
                    return Stream.concat(fromList, fromRuleName);
                })
                .filter(e -> e.getKey() != null && e.getValue() != null)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toCollection(LinkedHashSet::new))));

        List<ProductRule> platformList = grouped.entrySet().stream()
                .map(e -> new ProductRule(e.getKey(), new ArrayList<>(e.getValue())))
                .collect(Collectors.toList());

        EfgPlatformList out = new EfgPlatformList();
        out.setPublishDateTime(request.getPublishDateTime());
        out.setSourceSystem(request.getSourceSystem());
        out.setPlatformList(platformList);

        // send to Kafka (no key) — handle failures gracefully so the REST API doesn't fail when Kafka is down
        if (kafkaTemplate != null) {
            try {
                ListenableFuture<SendResult<String, EfgPlatformList>> future = kafkaTemplate.send(topic, out);
                future.addCallback(new ListenableFutureCallback<SendResult<String, EfgPlatformList>>() {
                    @Override
                    public void onSuccess(SendResult<String, EfgPlatformList> result) {
                        log.info("Message sent to topic {}: {}", topic, out);
                    }

                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Failed to send message to topic {}: {}", topic, ex.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("Exception while sending message to Kafka: {}", e.getMessage());
            }
        } else {
            log.warn("KafkaTemplate is null; skipping send");
        }
    }
}
