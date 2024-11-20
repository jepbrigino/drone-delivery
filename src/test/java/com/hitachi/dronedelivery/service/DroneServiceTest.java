package com.hitachi.dronedelivery.service;

import com.hitachi.dronedelivery.entity.Drone;
import com.hitachi.dronedelivery.enums.Model;
import com.hitachi.dronedelivery.enums.State;
import com.hitachi.dronedelivery.repository.DroneRepository;
import com.hitachi.dronedelivery.request.DroneRegisterRequest;
import com.hitachi.dronedelivery.response.CommonResponse;
import com.hitachi.dronedelivery.response.DroneDetails;
import com.hitachi.dronedelivery.util.MessageUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DroneServiceTest {
    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private DroneServiceImpl droneService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void givenNullRequest_whenRegisterDrone_thenThrowsException() {
        assertThrows(NullPointerException.class, () -> droneService.registerDrone(null));
        verifyNoInteractions(droneRepository);
        verifyNoInteractions(messageUtil);
    }

    @Test
    void givenInvalidDroneModel_whenTestRegisterDrone_thenReturnMessage() {
        DroneRegisterRequest request = DroneRegisterRequest.builder()
                .serialNumber("DRONE123")
                .model("AAAAA")
                .batteryPercentage(110)
                .build();

        assertThrows(IllegalArgumentException.class, () -> droneService.registerDrone(request));
        verifyNoInteractions(droneRepository);
        verifyNoInteractions(messageUtil);
    }
}
