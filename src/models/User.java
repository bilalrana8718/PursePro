//package models;
//
//public class User {
//    private String username;
//    private String email;
//    private String password;
//    private String phone;
//    
//
//    public User(String username, String email, String password, String phone) {
//        this.username = username;
//        this.email = email;
//        this.password = password;
//        this.phone = phone;
//        
//    }
//
//    // Getters and Setters
//    public String getUsername() {
//        return username;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//   
//}


package models;

public class User {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String phone;
    private String typeOfAccount;

    public User(int userId, String username, String email, String password, String phone, String typeOfAccount) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.typeOfAccount = typeOfAccount;
    }

    public User(String username, String email, String password, String phone, String typeOfAccount) {
        this(0, username, email, password, phone, typeOfAccount);
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }
}
