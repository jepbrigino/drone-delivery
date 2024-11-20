package com.hitachi.dronedelivery.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DroneRegisterRequest {

    @NotNull(message = "Serial number must not be null")
    private String serialNumber;

    @NotNull(message = "Model must not be null")
    private String model;

    @Min(value = 0, message = "Battery percentage not be less than 0")
    @Max(value = 100, message = "Battery percentage should not be greater than 100")
    @NotNull(message = "Battery percentage must not be null")
    private Integer batteryPercentage;
}
