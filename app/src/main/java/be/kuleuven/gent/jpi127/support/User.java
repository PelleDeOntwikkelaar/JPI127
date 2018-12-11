package be.kuleuven.gent.jpi127.support;

public class User {
    long id;
    String name;
    String email;
    Token token;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        id=0;
        token=null;
    }

    public User(long id, String name) {
        this.id = id;
        this.name = name;
        email=null;
        token=null;
    }

    public User(long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        token=null;
    }

    public User(long id, String name, Token token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
