package com.hitachi.dronedelivery.response;

import com.hitachi.dronedelivery.enums.Model;
import com.hitachi.dronedelivery.enums.State;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DroneDetails {
    private Long id;
    String serialNumber;
    Model model;
    Double loadWeight;
    Integer batteryPercentage;
    State state;
}
