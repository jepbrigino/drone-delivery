package com.hitachi.dronedelivery.entity;

import com.hitachi.dronedelivery.enums.Model;
import com.hitachi.dronedelivery.enums.State;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "DRONES")
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    String serialNumber;

    @Enumerated(EnumType.STRING)
    @NotNull
    Model model;

    @NotNull
    Double loadWeight;

    @NotNull
    Integer batteryPercentage;

    @Enumerated(EnumType.STRING)
    @NotNull
    State state;

    @OneToMany(mappedBy = "drone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Medicine> medicines = new ArrayList<>();
}
