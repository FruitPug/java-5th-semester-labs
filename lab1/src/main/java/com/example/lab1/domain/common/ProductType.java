package com.example.lab1.domain.common;

public enum ProductType {
    COSMIC_MILK("Cosmic Milk"),
    GALAXY_BALL_OF_YARN("Galaxy Ball of Yarn"),
    SPACE_CATNIP("Space Catnip");

    private final String displayName;

    ProductType(String displayName) {
        this.displayName = displayName;
    }

    public static ProductType fromDisplayName(String name) {
        for (ProductType productType : values()) {
            if (productType.displayName.equalsIgnoreCase(name)) {
                return productType;
            }
        }
        throw new IllegalArgumentException("Invalid product type name: " + name);
    }
}
