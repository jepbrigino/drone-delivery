package com.hitachi.dronedelivery.service;

import com.hitachi.dronedelivery.response.MedicineDetails;

import java.util.List;

public interface MedicineService {
    List<MedicineDetails> getMedicineByDroneId(Long droneId);
}
