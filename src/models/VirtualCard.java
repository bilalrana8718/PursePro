package models;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class VirtualCard {
    private int cardId;
    private int userId;
    private String cardNumber;
    private LocalDate expirationDate;
    private String cvv;
    private double cardLimit;
    private String cardStatus;
    private LocalDateTime issuedDate;

    public VirtualCard(int cardId, int userId, String cardNumber, LocalDate expirationDate, String cvv,
                       double cardLimit, String cardStatus, LocalDateTime issuedDate) {
        this.cardId = cardId;
        this.userId = userId;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.cardLimit = cardLimit;
        this.cardStatus = cardStatus;
        this.issuedDate = issuedDate;
    }

    // Getters and Setters
    public int getCardId() { return cardId; }
    public int getUserId() { return userId; }
    public String getCardNumber() { return cardNumber; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getCvv() { return cvv; }
    public double getCardLimit() { return cardLimit; }
    public String getCardStatus() { return cardStatus; }
    public LocalDateTime getIssuedDate() { return issuedDate; }
}
