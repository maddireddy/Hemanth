import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.ProducerRecord;

import com.pnc.efg.avro.schema.platformList.ProductRule;

public class JDK21Code {

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
    
}
