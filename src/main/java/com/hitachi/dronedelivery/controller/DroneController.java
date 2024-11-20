package com.hitachi.dronedelivery.controller;

import com.hitachi.dronedelivery.constant.Message;
import com.hitachi.dronedelivery.request.DroneRegisterRequest;
import com.hitachi.dronedelivery.response.CommonResponse;
import com.hitachi.dronedelivery.response.DroneDetails;
import com.hitachi.dronedelivery.response.MedicineDetails;
import com.hitachi.dronedelivery.service.DroneService;
import com.hitachi.dronedelivery.service.MedicineService;
import com.hitachi.dronedelivery.util.MessageUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/drones")
public class DroneController {
    private final DroneService droneService;

    private final MedicineService medicineService;

    public DroneController(DroneService droneService, MedicineService medicineService) {
        this.droneService = droneService;
        this.medicineService = medicineService;
    }

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<Object>> registerDrone(@Valid @RequestBody DroneRegisterRequest request) {
        // Validate request
        CommonResponse<Object> validationResponse = droneService.validateDroneRequest(request);

        // If validation fails, return a bad request response with error message
        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        // If validation passes, call the service to register the drone
        CommonResponse<Object> response = droneService.registerDrone(request);

        // Return a successful response
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<CommonResponse<Object>> getDrones() {
        return ResponseEntity.ok().body(CommonResponse.builder().message(MessageUtil.getMessage(Message.SUCCESS)).data(Arrays.asList(droneService.getAllDrones())).build());
    }

    @GetMapping("/{droneId}/status")
    public ResponseEntity<CommonResponse<Object>> getDroneStatus(@PathVariable Long droneId) {
        DroneDetails drone = droneService.getDroneById(droneId);
        if (drone == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageUtil.getMessage(Message.DRONE_NOT_FOUND, new String[]{droneId.toString()}));
        }

        return ResponseEntity.ok().body(CommonResponse.builder().message(MessageUtil.getDroneStatusMessage(drone.getState(), drone.getBatteryPercentage())).build());
    }

    @GetMapping("/{droneId}/information")
    public ResponseEntity<CommonResponse<Object>> getDroneInformation(@PathVariable Long droneId) {
        DroneDetails drone = droneService.getDroneById(droneId);
        if (drone == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageUtil.getMessage(Message.DRONE_NOT_FOUND, new String[]{droneId.toString()}));
        }

        return ResponseEntity.ok().body(CommonResponse.builder().message(MessageUtil.getMessage(Message.DRONE_BATTERY_PERCENTAGE, new String[]{drone.getBatteryPercentage().toString()})).data(Arrays.asList(drone)).build());
    }

    @PostMapping("/{droneId}/load")
    public ResponseEntity<CommonResponse<Object>> loadMedication(
            @RequestParam String name,
            @RequestParam(defaultValue = "0.0") Double weight,
            @RequestParam String code,
            @RequestParam MultipartFile image,
            @PathVariable Long droneId) {
        DroneDetails drone = droneService.getDroneById(droneId);
        if (drone == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, MessageUtil.getMessage(Message.DRONE_NOT_FOUND, new String[]{droneId.toString()}));
        }

        CommonResponse<Object> validationResponse = droneService.validateMedication(name, weight, code, drone);

        if (validationResponse != null) {
            return ResponseEntity.badRequest().body(validationResponse);
        }

        try {
            CommonResponse<Object> response = droneService.loadMedication(name, weight, code, image, drone);
            return ResponseEntity.created(null).body(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(CommonResponse.builder().message(e.getMessage()).build());
        }
    }

    @GetMapping("/{droneId}/load")
    public ResponseEntity<CommonResponse<Object>> getLoadedMedication(@PathVariable Long droneId) {
        List<MedicineDetails> medicines = medicineService.getMedicineByDroneId(droneId);
        return ResponseEntity.ok().body(CommonResponse.builder().message(MessageUtil.getMessage(Message.DRONE_LOAD_FETCHED)).data(Arrays.asList(medicines)).build());
    }
}
