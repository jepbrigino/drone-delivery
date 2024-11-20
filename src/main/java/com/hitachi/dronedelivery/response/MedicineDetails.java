package com.hitachi.dronedelivery.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MedicineDetails {
    private String name;
    private Double weight;
    private String code;
}
