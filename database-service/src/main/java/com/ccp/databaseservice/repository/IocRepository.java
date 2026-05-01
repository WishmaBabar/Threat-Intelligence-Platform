package com.ccp.databaseservice.repository;

import com.ccp.databaseservice.entity.IocEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IocRepository extends JpaRepository<IocEntity, Long> {
    List<IocEntity> findByType(String type);
    List<IocEntity> findBySeverityGreaterThan(double min);
    List<IocEntity> findByTypeAndSeverityGreaterThan(String type, double min);
    @Query("SELECT e.type, COUNT(e) FROM IocEntity e GROUP BY e.type")
    List<Object[]> countByType();
    @Query("SELECT COUNT(e) FROM IocEntity e WHERE e.severity > :threshold")
    long countHighSeverity(@Param("threshold") double threshold);
}
