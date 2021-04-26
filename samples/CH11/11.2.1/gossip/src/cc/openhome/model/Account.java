package cc.openhome.model;

public class Account {
    private String name;
    private String email;
    private String encrypt;
    private String salt;
    
    public Account(String name, String email, String encrypt, String salt) {
        this.name = name;
        this.email = email;
        this.encrypt = encrypt;
        this.salt = salt;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public String getSalt() {
        return salt;
    }
}
