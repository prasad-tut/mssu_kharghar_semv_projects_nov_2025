import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineDAO {
    
    // CREATE - Add new medicine to database
    public boolean addMedicine(Medicine medicine) {
        String query = "INSERT INTO medicines (medicine_name, manufacturer, quantity, price, expiry_date, category) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, medicine.getMedicineName());
            pstmt.setString(2, medicine.getManufacturer());
            pstmt.setInt(3, medicine.getQuantity());
            pstmt.setDouble(4, medicine.getPrice());
            pstmt.setDate(5, medicine.getExpiryDate());
            pstmt.setString(6, medicine.getCategory());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error adding medicine!");
            e.printStackTrace();
            return false;
        }
    }
    
    // READ - Get all medicines from database
    public List<Medicine> getAllMedicines() {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM medicines";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Medicine medicine = new Medicine(
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    rs.getString("manufacturer"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDate("expiry_date"),
                    rs.getString("category")
                );
                medicines.add(medicine);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving medicines!");
            e.printStackTrace();
        }
        
        return medicines;
    }
    
    // READ - Get medicine by ID
    public Medicine getMedicineById(int medicineId) {
        String query = "SELECT * FROM medicines WHERE medicine_id = ?";
        Medicine medicine = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                medicine = new Medicine(
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    rs.getString("manufacturer"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDate("expiry_date"),
                    rs.getString("category")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving medicine by ID!");
            e.printStackTrace();
        }
        
        return medicine;
    }
    
    // UPDATE - Update existing medicine
    public boolean updateMedicine(Medicine medicine) {
        String query = "UPDATE medicines SET medicine_name = ?, manufacturer = ?, quantity = ?, price = ?, expiry_date = ?, category = ? WHERE medicine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, medicine.getMedicineName());
            pstmt.setString(2, medicine.getManufacturer());
            pstmt.setInt(3, medicine.getQuantity());
            pstmt.setDouble(4, medicine.getPrice());
            pstmt.setDate(5, medicine.getExpiryDate());
            pstmt.setString(6, medicine.getCategory());
            pstmt.setInt(7, medicine.getMedicineId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error updating medicine!");
            e.printStackTrace();
            return false;
        }
    }
    
    // DELETE - Remove medicine from database
    public boolean deleteMedicine(int medicineId) {
        String query = "DELETE FROM medicines WHERE medicine_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, medicineId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.out.println("Error deleting medicine!");
            e.printStackTrace();
            return false;
        }
    }
    
    // SEARCH - Search medicines by name
    public List<Medicine> searchMedicineByName(String name) {
        List<Medicine> medicines = new ArrayList<>();
        String query = "SELECT * FROM medicines WHERE medicine_name LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Medicine medicine = new Medicine(
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    rs.getString("manufacturer"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getDate("expiry_date"),
                    rs.getString("category")
                );
                medicines.add(medicine);
            }
            
        } catch (SQLException e) {
            System.out.println("Error searching medicines!");
            e.printStackTrace();
        }
        
        return medicines;
    }
}