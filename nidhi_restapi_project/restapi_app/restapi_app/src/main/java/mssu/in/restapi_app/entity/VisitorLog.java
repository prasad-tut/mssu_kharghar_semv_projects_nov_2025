package mssu.in.restapi_app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visitor_log")
public class VisitorLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "visitor_name", nullable = false, length = 100)
    private String visitorName;
    
    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;
    
    @Column(length = 100)
    private String email;
    
    @Column(nullable = false, length = 200)
    private String purpose;
    
    @Column(name = "host_name", nullable = false, length = 100)
    private String hostName;
    
    @Column(length = 100)
    private String department;
    
    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visitor_type", nullable = false, length = 20)
    private VisitorType visitorType;
    
    @Column(name = "id_proof_type", length = 50)
    private String idProofType;
    
    @Column(name = "id_proof_number", length = 50)
    private String idProofNumber;
    
    @Column(columnDefinition = "TEXT")
    private String remarks;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public VisitorLog() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public VisitorLog(String visitorName, String contactNumber, String email, String purpose,
                     String hostName, String department, LocalDateTime checkInTime,
                     VisitorType visitorType, String idProofType, String idProofNumber) {
        this.visitorName = visitorName;
        this.contactNumber = contactNumber;
        this.email = email;
        this.purpose = purpose;
        this.hostName = hostName;
        this.department = department;
        this.checkInTime = checkInTime;
        this.visitorType = visitorType;
        this.idProofType = idProofType;
        this.idProofNumber = idProofNumber;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getVisitorName() {
        return visitorName;
    }
    
    public void setVisitorName(String visitorName) {
        this.visitorName = visitorName;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }
    
    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = checkInTime;
    }
    
    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }
    
    public void setCheckOutTime(LocalDateTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }
    
    public VisitorType getVisitorType() {
        return visitorType;
    }
    
    public void setVisitorType(VisitorType visitorType) {
        this.visitorType = visitorType;
    }
    
    public String getIdProofType() {
        return idProofType;
    }
    
    public void setIdProofType(String idProofType) {
        this.idProofType = idProofType;
    }
    
    public String getIdProofNumber() {
        return idProofNumber;
    }
    
    public void setIdProofNumber(String idProofNumber) {
        this.idProofNumber = idProofNumber;
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
