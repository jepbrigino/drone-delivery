package com.hitachi.dronedelivery.repository;

import com.hitachi.dronedelivery.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    public List<Medicine> findByDroneId(Long droneId);
}
