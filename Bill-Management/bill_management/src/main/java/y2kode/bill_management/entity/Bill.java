package y2kode.bill_management.entity;

import java.time.LocalDateTime;

public class Bill {
    private Integer id;
    private String biller;
    private String description;
    private String amount;
    private LocalDateTime billDate = LocalDateTime.now();
    private LocalDateTime paymentDate = LocalDateTime.now();
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;

    public Bill() {}
    
    public Bill(Integer id, String biller, String description, String amount, 
                LocalDateTime billDate, LocalDateTime paymentDate, 
                PaymentMode paymentMode, PaymentStatus paymentStatus) {
        super();
        this.id = id;
        this.biller = biller;
        this.description = description;
        this.amount = amount;
        this.setBillDate(billDate);
        this.setPaymentDate(paymentDate);
        this.paymentMode = paymentMode;
        this.paymentStatus = paymentStatus;
    }

    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBiller() {
        return biller;
    }

    public void setBiller(String biller) {
        this.biller = biller;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(PaymentMode paymentMode) {
        this.paymentMode = paymentMode;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDateTime billDate) {
        this.billDate = billDate;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    @Override
    public String toString() {
        return "Bill [id=" + id + ", biller=" + biller + ", description=" + description + 
               ", amount=" + amount + ", billDate=" + billDate + ", paymentDate=" + paymentDate + 
               ", paymentMode=" + paymentMode + ", paymentStatus=" + paymentStatus + "]";
    }

}

