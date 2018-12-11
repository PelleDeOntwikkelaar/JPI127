package be.kuleuven.gent.jpi127.support;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class is used to encrypt entered passwords
 *
 * @author Pelle Reyniers
 */
public class Encryptie {

    private Encryptie() {
    }

    /**
     *
     * @param password
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    public static String encodeSHA256(String password) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(password.getBytes("UTF-8"));
        byte[] digest = md.digest();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Base64.encodeToString(digest,0));
        int lengte = stringBuffer.length();
        stringBuffer.deleteCharAt(lengte-1);
        return stringBuffer.toString();

    }

}