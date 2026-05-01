package com.ccp.kafkaproducerservice.controller;

import com.ccp.kafkaproducerservice.dto.IocRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProducerController {
    @Autowired
    private KafkaTemplate<String, IocRequest> kafkaTemplate;

    @PostMapping("/iocs")
    public ResponseEntity<String> publish(@RequestBody List<IocRequest> iocs) {
        iocs.forEach(ioc -> kafkaTemplate.send("iocs", ioc));
        return ResponseEntity.ok("Published " + iocs.size() + " IOCs");
    }
}
