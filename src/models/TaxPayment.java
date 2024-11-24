package models;

import javafx.beans.property.*;
public class TaxPayment {
    private final SimpleStringProperty startDate;
    private final SimpleStringProperty endDate;
    private final SimpleDoubleProperty taxPaid;
    private final SimpleStringProperty paymentDate;

    public TaxPayment(String startDate, String endDate, double taxPaid, String paymentDate) {
        this.startDate = new SimpleStringProperty(startDate);
        this.endDate = new SimpleStringProperty(endDate);
        this.taxPaid = new SimpleDoubleProperty(taxPaid);
        this.paymentDate = new SimpleStringProperty(paymentDate);
    }

    public String getStartDate() {
        return startDate.get();
    }

    public void setStartDate(String startDate) {
        this.startDate.set(startDate);
    }

    public String getEndDate() {
        return endDate.get();
    }

    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
    }

    public double getTaxPaid() {
        return taxPaid.get();
    }

    public void setTaxPaid(double taxPaid) {
        this.taxPaid.set(taxPaid);
    }

    public String getPaymentDate() {
        return paymentDate.get();
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate.set(paymentDate);
    }
}
