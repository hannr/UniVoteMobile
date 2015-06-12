package ch.bfh.univote.registration;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ch.bfh.univote.election.model.PrivateKeyJSONContainer;

/**
 * Component for password-based AES encryption and decryption
 * @author Raphael Haenni
 */
public class AES {
    private static final String ALGORITHM = "AES";

    /**
     * Encrypts a message based on a password.
     */
    public static String encrypt(String message, String password) throws Exception {
        SecureRandom sr = new SecureRandom();
        long padding = sr.nextLong();

        PrivateKeyJSONContainer pkjc = new PrivateKeyJSONContainer(message, padding);
        String jsonString = new Gson().toJson(pkjc);

        byte[] bytePassword = password.getBytes(Charset.forName("UTF-8"));
        byte[] byteJson = jsonString.getBytes(Charset.forName("UTF-8"));

        Cipher c = Cipher.getInstance(ALGORITHM);
        SecretKey k = new SecretKeySpec(AES.makeKey(bytePassword), ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, k);

        byte[] encryptedMessage = c.doFinal(byteJson);

        return Base64.encodeToString(encryptedMessage, Base64.DEFAULT);
    }

    /**
     *
     * @param message
     * @param password
     * @return decrypted message as utf-8 string if password correct. otherwise return null.
     * @throws Exception
     */
    public static String decrypt(String message, String password) throws Exception {
        byte[] byteMessage = Base64.decode(message, Base64.DEFAULT);
        byte[] bytePassword = password.getBytes(Charset.forName("UTF-8"));

        Cipher c = Cipher.getInstance(ALGORITHM);
        SecretKeySpec k = new SecretKeySpec(AES.makeKey(bytePassword), ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, k);

        byte[] decryptedValue = c.doFinal(byteMessage);

        String decryptedJsonString = new String(decryptedValue, "UTF-8");

        // if no jsoncontainer object, exception gets thrown and decryption failed!
        PrivateKeyJSONContainer pkjc = new Gson().fromJson(decryptedJsonString, PrivateKeyJSONContainer.class);
        String returnValue = pkjc.getPrivateKey();

        return returnValue;
    }

    private static byte[] makeKey(byte[] bkey) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(bkey);
        return md.digest();
    }
}