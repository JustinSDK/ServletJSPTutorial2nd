package cc.openhome.model;

public class Account {
    private String name;
    private String email;
    private String encrypt;
    
    public Account(String name, String email, String encrypt) {
        this.name = name;
        this.email = email;
        this.encrypt = encrypt;
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
}
