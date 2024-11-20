package com.hitachi.dronedelivery.service;

import com.hitachi.dronedelivery.enums.State;
import com.hitachi.dronedelivery.request.DroneRegisterRequest;
import com.hitachi.dronedelivery.response.CommonResponse;
import com.hitachi.dronedelivery.response.DroneDetails;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DroneService {
    CommonResponse<Object> registerDrone(DroneRegisterRequest request);

    List<DroneDetails> getAllDrones();

    DroneDetails getDroneById(Long id);

    CommonResponse<Object> loadMedication(String name, Double weight, String code, MultipartFile image, DroneDetails droneDetails) throws IOException;

    int simulateBattery(State state, int currentBattery);

    CommonResponse<Object> validateMedication(String name, Double weight, String code, DroneDetails drone);

    CommonResponse<Object> validateDroneRequest(DroneRegisterRequest request);


}
