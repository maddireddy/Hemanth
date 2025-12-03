package com.pnc.efg.controller;

import com.pnc.efg.dto.PublishRequest;
import com.pnc.efg.service.EfgService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/efg")
public class EfgController {

    private final EfgService service;

    public EfgController(EfgService service) {
        this.service = service;
    }

    @PostMapping("/publish")
    public ResponseEntity<String> publish(@RequestBody PublishRequest request) {
        service.publish(request);
        return ResponseEntity.ok("published");
    }
}
