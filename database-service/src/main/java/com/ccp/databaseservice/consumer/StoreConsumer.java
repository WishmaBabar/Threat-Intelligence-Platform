package com.ccp.databaseservice.consumer;

import com.ccp.databaseservice.dto.EnrichedIoc;
import com.ccp.databaseservice.entity.IocEntity;
import com.ccp.databaseservice.repository.IocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class StoreConsumer {
    @Autowired
    private IocRepository repo;

    @KafkaListener(topics = "enriched-iocs", groupId = "db-group")
    public void store(EnrichedIoc dto) {
        try {
            IocEntity entity = new IocEntity();
            entity.setType(dto.getType());
            entity.setValue(dto.getValue());
            entity.setSeverity(dto.getSeverity());
            entity.setCreatedAt(dto.getCreatedAt());
            repo.save(entity);
            System.out.println("Saved IOC: " + entity.getValue() + " with severity " + entity.getSeverity());
        } catch (Exception e) {
            System.err.println("Error saving IOC: " + e.getMessage());
        }
    }
}
