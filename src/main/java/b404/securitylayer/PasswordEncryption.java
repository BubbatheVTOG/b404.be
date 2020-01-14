package b404.securitylayer;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import javax.xml.bind.DatatypeConverter;

public class PasswordEncryption {
    public static String encrypt(String password) {
        return password;
    }

    /*public static String[] encrypt(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        String [] output = {toHex(salt), toHex(hash)};
        return output;
    }

    public static String[] encrypt(String password, String salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();

        PBEKeySpec spec = new PBEKeySpec(chars, salt.getBytes(), iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();

        String [] output = {toHex(salt.getBytes()), toHex(hash)};
        System.out.println(decodeHex(toHex(salt.getBytes())));
        return output;
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte [] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bigInteger = new BigInteger(1, array);
        String hex = bigInteger.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0" +paddingLength+ "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static String decodeHex(String salt) {
        byte[] bytes = DatatypeConverter.parseHexBinary(salt);
        try {
            String result = new String(bytes, "UTF-8");
            return result;
        } catch(UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }*/
}