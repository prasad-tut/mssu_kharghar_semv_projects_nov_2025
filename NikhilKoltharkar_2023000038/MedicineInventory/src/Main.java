import java.sql.Date;
import java.util.List;
import java.util.Scanner;

public class Main {
    
    public static void main(String[] args) {
        MedicineDAO dao = new MedicineDAO();
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n========================================");
            System.out.println("   MEDICINE INVENTORY MANAGEMENT SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Add New Medicine");
            System.out.println("2. View All Medicines");
            System.out.println("3. Search Medicine by ID");
            System.out.println("4. Update Medicine");
            System.out.println("5. Delete Medicine");
            System.out.println("6. Search Medicine by Name");
            System.out.println("7. Exit");
            System.out.println("========================================");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    addMedicine(dao, scanner);
                    break;
                case 2:
                    viewAllMedicines(dao);
                    break;
                case 3:
                    searchMedicineById(dao, scanner);
                    break;
                case 4:
                    updateMedicine(dao, scanner);
                    break;
                case 5:
                    deleteMedicine(dao, scanner);
                    break;
                case 6:
                    searchMedicineByName(dao, scanner);
                    break;
                case 7:
                    System.out.println("Thank you for using Medicine Inventory System!");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    // Method to add new medicine
    private static void addMedicine(MedicineDAO dao, Scanner scanner) {
        System.out.println("\n--- ADD NEW MEDICINE ---");
        
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter Manufacturer: ");
        String manufacturer = scanner.nextLine();
        
        System.out.print("Enter Quantity: ");
        int quantity = scanner.nextInt();
        
        System.out.print("Enter Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        
        System.out.print("Enter Expiry Date (YYYY-MM-DD): ");
        String expiryDateStr = scanner.nextLine();
        Date expiryDate = Date.valueOf(expiryDateStr);
        
        System.out.print("Enter Category: ");
        String category = scanner.nextLine();
        
        Medicine medicine = new Medicine(name, manufacturer, quantity, price, expiryDate, category);
        
        if (dao.addMedicine(medicine)) {
            System.out.println("✓ Medicine added successfully!");
        } else {
            System.out.println("✗ Failed to add medicine!");
        }
    }
    
    // Method to view all medicines
    private static void viewAllMedicines(MedicineDAO dao) {
        System.out.println("\n--- ALL MEDICINES ---");
        List<Medicine> medicines = dao.getAllMedicines();
        
        if (medicines.isEmpty()) {
            System.out.println("No medicines found in inventory!");
        } else {
            for (Medicine medicine : medicines) {
                System.out.println(medicine);
            }
            System.out.println("Total Medicines: " + medicines.size());
        }
    }
    
    // Method to search medicine by ID
    private static void searchMedicineById(MedicineDAO dao, Scanner scanner) {
        System.out.println("\n--- SEARCH MEDICINE BY ID ---");
        System.out.print("Enter Medicine ID: ");
        int id = scanner.nextInt();
        
        Medicine medicine = dao.getMedicineById(id);
        
        if (medicine != null) {
            System.out.println("\nMedicine Found:");
            System.out.println(medicine);
        } else {
            System.out.println("✗ Medicine with ID " + id + " not found!");
        }
    }
    
    // Method to update medicine
    private static void updateMedicine(MedicineDAO dao, Scanner scanner) {
        System.out.println("\n--- UPDATE MEDICINE ---");
        System.out.print("Enter Medicine ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        Medicine existingMedicine = dao.getMedicineById(id);
        
        if (existingMedicine == null) {
            System.out.println("✗ Medicine with ID " + id + " not found!");
            return;
        }
        
        System.out.println("\nCurrent Details:");
        System.out.println(existingMedicine);
        
        System.out.println("\nEnter new details (press Enter to keep current value):");
        
        System.out.print("Enter Medicine Name [" + existingMedicine.getMedicineName() + "]: ");
        String name = scanner.nextLine();
        if (!name.isEmpty()) {
            existingMedicine.setMedicineName(name);
        }
        
        System.out.print("Enter Manufacturer [" + existingMedicine.getManufacturer() + "]: ");
        String manufacturer = scanner.nextLine();
        if (!manufacturer.isEmpty()) {
            existingMedicine.setManufacturer(manufacturer);
        }
        
        System.out.print("Enter Quantity [" + existingMedicine.getQuantity() + "]: ");
        String quantityStr = scanner.nextLine();
        if (!quantityStr.isEmpty()) {
            existingMedicine.setQuantity(Integer.parseInt(quantityStr));
        }
        
        System.out.print("Enter Price [" + existingMedicine.getPrice() + "]: ");
        String priceStr = scanner.nextLine();
        if (!priceStr.isEmpty()) {
            existingMedicine.setPrice(Double.parseDouble(priceStr));
        }
        
        System.out.print("Enter Expiry Date (YYYY-MM-DD) [" + existingMedicine.getExpiryDate() + "]: ");
        String expiryDateStr = scanner.nextLine();
        if (!expiryDateStr.isEmpty()) {
            existingMedicine.setExpiryDate(Date.valueOf(expiryDateStr));
        }
        
        System.out.print("Enter Category [" + existingMedicine.getCategory() + "]: ");
        String category = scanner.nextLine();
        if (!category.isEmpty()) {
            existingMedicine.setCategory(category);
        }
        
        if (dao.updateMedicine(existingMedicine)) {
            System.out.println("✓ Medicine updated successfully!");
        } else {
            System.out.println("✗ Failed to update medicine!");
        }
    }
    
    // Method to delete medicine
    private static void deleteMedicine(MedicineDAO dao, Scanner scanner) {
        System.out.println("\n--- DELETE MEDICINE ---");
        System.out.print("Enter Medicine ID to delete: ");
        int id = scanner.nextInt();
        
        Medicine medicine = dao.getMedicineById(id);
        
        if (medicine == null) {
            System.out.println("✗ Medicine with ID " + id + " not found!");
            return;
        }
        
        System.out.println("\nMedicine to be deleted:");
        System.out.println(medicine);
        
        System.out.print("Are you sure you want to delete? (yes/no): ");
        scanner.nextLine(); // Consume newline
        String confirmation = scanner.nextLine();
        
        if (confirmation.equalsIgnoreCase("yes")) {
            if (dao.deleteMedicine(id)) {
                System.out.println("✓ Medicine deleted successfully!");
            } else {
                System.out.println("✗ Failed to delete medicine!");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }
    
    // Method to search medicine by name
    private static void searchMedicineByName(MedicineDAO dao, Scanner scanner) {
        System.out.println("\n--- SEARCH MEDICINE BY NAME ---");
        System.out.print("Enter Medicine Name: ");
        String name = scanner.nextLine();
        
        List<Medicine> medicines = dao.searchMedicineByName(name);
        
        if (medicines.isEmpty()) {
            System.out.println("✗ No medicines found with name containing '" + name + "'");
        } else {
            System.out.println("\nMedicines Found:");
            for (Medicine medicine : medicines) {
                System.out.println(medicine);
            }
            System.out.println("Total Results: " + medicines.size());
        }
    }
}