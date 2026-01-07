package me.heroicstudios.cannon;

public enum StrikeType {
    ORBITAL_SINGLE("Single Strike", "§7Single orbital strike"),
    ORBITAL_RING("Ring Strike", "§7Multiple ring orbital strike"),
    ORBITAL_DRILL("Bunker Buster", "§7Deep vertical drilling strike");

    private final String displayName;
    private final String description;

    StrikeType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static StrikeType fromString(String value) {
        if (value == null) return ORBITAL_RING;
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ORBITAL_RING;
        }
    }
}