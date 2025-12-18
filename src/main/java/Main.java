package com.pnc.efg.kafka;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
        List<Map<String, String>> inputList = new ArrayList<>();

        // Entry 1
        Map<String, String> map1 = new HashMap<>();
        map1.put("ruleName", "Rule#1");
        map1.put("product", "Zelle-Send-Decline");
        inputList.add(map1);

        // Entry 2
        Map<String, String> map2 = new HashMap<>();
        map2.put("ruleName", "Rule#2");
        map2.put("product", "SSW_Domestic-Send-Hold");
        inputList.add(map2);

        // Entry 3
        Map<String, String> map3 = new HashMap<>();
        map3.put("ruleName", "Rule#3");
        map3.put("product", "Zelle-Send-Decline");
        inputList.add(map3);

        // Initialize controller
        AvroConvertController controller = new AvroConvertController();
        controller.processAndPublish(inputList);
    }
}
