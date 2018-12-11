package be.kuleuven.gent.jpi127.support;

import java.sql.Date;

/**
 * This class represents a Token.
 *
 * @author Pelle Reyneirs
 */
public class Token {
    private String token;
    private Date datum;

    public Token() {
    }

    public Token(String token, Date datum) {
        this.token = token;
        this.datum = datum;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
}
