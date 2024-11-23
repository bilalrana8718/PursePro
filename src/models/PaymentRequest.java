package models;

import javafx.beans.property.*;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentRequest {

    private final IntegerProperty requestId;
    private final DoubleProperty amount;
    private final StringProperty status;
    private final ObjectProperty<LocalDateTime> dateCreated;
    private final StringProperty senderName;
    private final StringProperty recipientName;

    public PaymentRequest(int requestId, double amount, String status, LocalDateTime dateCreated, String senderName, String recipientName) {
        this.requestId = new SimpleIntegerProperty(requestId);
        this.amount = new SimpleDoubleProperty(amount);
        this.status = new SimpleStringProperty(status);
        this.dateCreated = new SimpleObjectProperty<>(dateCreated);
        this.senderName = new SimpleStringProperty(senderName);
        this.recipientName = new SimpleStringProperty(recipientName);
    }

    public int getRequestId() { return requestId.get(); }
    public IntegerProperty requestIdProperty() { return requestId; }

    public double getAmount() { return amount.get(); }
    public DoubleProperty amountProperty() { return amount; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }

    public LocalDateTime getDateCreated() { return dateCreated.get(); }
    public ObjectProperty<LocalDateTime> dateCreatedProperty() { return dateCreated; }

    public String getSenderName() { return senderName.get(); }
    public StringProperty senderNameProperty() { return senderName; }

    public String getRecipientName() { return recipientName.get(); }
    public StringProperty recipientNameProperty() { return recipientName; }
}
