package mssu.in.restapi_app.entity;

/**
 * Represents different types of products in the system.
 * Each type has a display name and a description for better user experience.
 */
public enum ProductType {
    /**
     * Consumable products like food, beverages, and household items
     */
    CONSUMABLE("Consumable", "Products that are consumed or used up"),
    
    /**
     * Electronic devices and gadgets
     */
    ELECTRONIC("Electronic", "Electronic devices and gadgets"),
    
    /**
     * Vehicles and automotive parts
     */
    AUTOMOBILE("Automobile", "Vehicles and automotive parts"),
    
    /**
     * Pharmaceutical and healthcare products
     */
    MEDICINE("Medicine", "Pharmaceutical and healthcare products"),
    
    /**
     * Clothing and fashion accessories
     */
    APPAREL("Apparel", "Clothing and fashion accessories"),
    
    /**
     * Books, magazines, and other reading materials
     */
    BOOKS("Books", "Books and reading materials");

    private final String displayName;
    private final String description;

    ProductType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /**
     * Gets the display name of the product type
     * @return A user-friendly display name
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the description of the product type
     * @return A brief description of what this product type represents
     */
    public String getDescription() {
        return description;
    }

    /**
     * Finds a ProductType by its name (case-insensitive)
     * @param name The name to search for
     * @return The matching ProductType or null if not found
     */
    public static ProductType fromName(String name) {
        if (name == null) {
            return null;
        }
        for (ProductType type : values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    /**
     * Checks if a string is a valid product type
     * @param name The name to check
     * @return true if the name matches a valid ProductType (case-insensitive)
     */
    public static boolean isValid(String name) {
        return fromName(name) != null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
