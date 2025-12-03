package com.pnc.efg.service;

import com.pnc.efg.avro.schema.platformList.EfgPlatformList;
import com.pnc.efg.avro.schema.platformList.ProductRule;
import com.pnc.efg.dto.ItemDto;
import com.pnc.efg.dto.PublishRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EfgServiceTest {

    private KafkaTemplate<String, EfgPlatformList> kafkaTemplate;
    private EfgService service;

    @BeforeEach
    void setup() {
        kafkaTemplate = Mockito.mock(KafkaTemplate.class);
        service = new EfgService(kafkaTemplate, "efg-platform-list");
    }

    @Test
    void publish_groupsItemsAndSends() {
        ItemDto a1 = new ItemDto("P1", Arrays.asList("r1", "r2"));
        ItemDto a2 = new ItemDto("P1", Arrays.asList("r3"));
        ItemDto b1 = new ItemDto("P2", Collections.singletonList("r4"));

        PublishRequest req = new PublishRequest("2025-12-03T10:00:00Z", "SYS", Arrays.asList(a1, a2, b1));

        service.publish(req);

        ArgumentCaptor<EfgPlatformList> captor = ArgumentCaptor.forClass(EfgPlatformList.class);
        verify(kafkaTemplate, times(1)).send(eq("efg-platform-list"), captor.capture());

        EfgPlatformList sent = captor.getValue();
        assertEquals("2025-12-03T10:00:00Z", sent.getPublishDateTime());
        assertEquals("SYS", sent.getSourceSystem());
        assertNotNull(sent.getPlatformList());
        // Expect two product rules: P1 and P2
        assertEquals(2, sent.getPlatformList().size());

        ProductRule p1 = sent.getPlatformList().stream().filter(pr -> "P1".equals(pr.getProduct())).findFirst().orElse(null);
        ProductRule p2 = sent.getPlatformList().stream().filter(pr -> "P2".equals(pr.getProduct())).findFirst().orElse(null);

        assertNotNull(p1);
        assertNotNull(p2);
        assertEquals(3, p1.getRules().size()); // r1,r2,r3
        assertEquals(1, p2.getRules().size());
    }
}
