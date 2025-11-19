import java.sql.Date;

public class Medicine {
    // Properties of a medicine
    private int medicineId;
    private String medicineName;
    private String manufacturer;
    private int quantity;
    private double price;
    private Date expiryDate;
    private String category;
    
    // Constructor - used when creating a new medicine
    public Medicine(int medicineId, String medicineName, String manufacturer, 
                   int quantity, double price, Date expiryDate, String category) {
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.category = category;
    }
    
    // Constructor without ID - used when adding new medicine (ID is auto-generated)
    public Medicine(String medicineName, String manufacturer, 
                   int quantity, double price, Date expiryDate, String category) {
        this.medicineName = medicineName;
        this.manufacturer = manufacturer;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.category = category;
    }
    
    // Getter methods - to read the values
    public int getMedicineId() {
        return medicineId;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public String getCategory() {
        return category;
    }
    
    // Setter methods - to change the values
    public void setMedicineId(int medicineId) {
        this.medicineId = medicineId;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    // toString method - to display medicine information nicely
    @Override
    public String toString() {
        return "Medicine ID: " + medicineId + 
               "\nName: " + medicineName + 
               "\nManufacturer: " + manufacturer + 
               "\nQuantity: " + quantity + 
               "\nPrice: $" + price + 
               "\nExpiry Date: " + expiryDate + 
               "\nCategory: " + category + 
               "\n" + "-".repeat(50);
    }
}