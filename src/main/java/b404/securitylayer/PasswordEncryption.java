package b404.securitylayer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class PasswordEncryption {
    public static String encrypt(String password) {
        return password;
        /*try {
            // Generating salt
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);

            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), bytes, 65536, 512);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();

            String hashedPassword = new String(hash);
            String salt = new String(bytes);
            String [] output = {hashedPassword, salt};

            return output;
        } catch (InvalidKeySpecException ikse) {
            throw new RuntimeException(ikse);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }
    }

    public static String[] encrypt(String password, String salt) {
        //return password
        byte[] bytes = salt.getBytes();
        try {
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), bytes, 65536, 512);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = secretKeyFactory.generateSecret(keySpec).getEncoded();

            String hashedPassword = new String(hash);
            String [] output = {hashedPassword, salt};

            return output;
        } catch (InvalidKeySpecException ikse) {
            throw new RuntimeException(ikse);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }*/
    }
}
