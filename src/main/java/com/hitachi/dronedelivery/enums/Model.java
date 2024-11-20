package com.hitachi.dronedelivery.enums;

public enum Model {
    LIGHTWEIGHT(250),  // Max weight in grams
    MIDDLEWEIGHT(500),
    CRUISERWEIGHT(750),
    HEAVYWEIGHT(1000);

    private final int maxWeight;

    Model(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    // Getter to retrieve the max weight
    public Double getMaxWeight() {
        return Double.valueOf(maxWeight);
    }
}

