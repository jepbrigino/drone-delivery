package com.hitachi.dronedelivery.service;

import com.hitachi.dronedelivery.entity.Medicine;
import com.hitachi.dronedelivery.repository.MedicineRepository;
import com.hitachi.dronedelivery.response.MedicineDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

    public MedicineServiceImpl(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<MedicineDetails> getMedicineByDroneId(Long droneId) {
        List<Medicine> medicines = medicineRepository.findByDroneId(droneId);
        return medicines.stream()
                .map(medicine -> MedicineDetails.builder()
                        .name(medicine.getName())
                        .weight(medicine.getWeight())
                        .code(medicine.getCode())
                        .build())
                .collect(Collectors.toList());
    }
}
