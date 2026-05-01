package com.ccp.databaseservice.controller;

import com.ccp.databaseservice.entity.IocEntity;
import com.ccp.databaseservice.repository.IocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class QueryController {
    @Autowired
    private IocRepository repo;

    @GetMapping("/iocs")
    public List<IocEntity> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false, name = "minSeverity") Double min) {
        if (type != null && min != null) return repo.findByTypeAndSeverityGreaterThan(type, min);
        if (type != null) return repo.findByType(type);
        if (min != null) return repo.findBySeverityGreaterThan(min);
        return repo.findAll();
    }

    @GetMapping("/iocs/{id}")
    public IocEntity getById(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }

    @GetMapping("/analytics/summary")
    public Map<String, Long> summary() {
        List<Object[]> rows = repo.countByType();
        Map<String, Long> result = new HashMap<>();
        rows.forEach(r -> result.put((String) r[0], (Long) r[1]));
        return result;
    }

    @GetMapping("/analytics/high-severity")
    public long highSeverity(@RequestParam(defaultValue = "7.0") double threshold) {
        return repo.countHighSeverity(threshold);
    }
}
