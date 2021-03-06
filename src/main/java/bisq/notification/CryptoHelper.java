package bisq.notification;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {

    private IvParameterSpec ivspec;
    private SecretKeySpec keyspec;
    private Cipher cipher;
    private String key; // 32 character key - exchanged with phones that receive the message

    public CryptoHelper(String key_) {
        key = key_;

        keyspec = new SecretKeySpec(key.getBytes(), "AES");

        try {
            cipher = Cipher.getInstance("AES/CBC/NOPadding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String valueToEncrypt, String iv) throws Exception {
        if ( iv.length() != 16) { throw new Exception( "iv not 16 characters"); }
        ivspec = new IvParameterSpec(iv.getBytes());
        return Base64.encode(encryptInternal(valueToEncrypt, ivspec));
    }

    public String decrypt(String valueToDecrypt, String iv) throws Exception {
        if ( iv.length() != 16) { throw new Exception( "iv not 16 characters"); }
        ivspec = new IvParameterSpec(iv.getBytes());
        return new String(decryptInternal(valueToDecrypt, ivspec));
    }

    private byte[] encryptInternal(String text, IvParameterSpec ivspec) throws Exception {
        if (text == null || text.length() == 0) {
            throw new Exception("Empty string");
        }

        if (key.length() != 32) { throw new Exception("key not 32 characters"); }

        byte[] encrypted = null;
        try {
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            encrypted = cipher.doFinal(text.getBytes());
        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }
        return encrypted;
    }

    private byte[] decryptInternal(String code, IvParameterSpec ivspec) throws Exception {
        if (code == null || code.length() == 0) {
            throw new Exception("Empty string");
        }

        if (key.length() != 32) { throw new Exception("key not 32 characters"); }

        byte[] decrypted = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
            decrypted = cipher.doFinal(Base64.decode(code));
        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }
        return decrypted;
    }
}