package blink.utility.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.math.BigInteger;

/**
 * Utility class for handling salt generation and password hashing
 */
public class PasswordEncryption {
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int NUM_ITERATIONS = 1000;
    private static final int KEY_LENGTH = 64 * 8;

    private PasswordEncryption(){
        //Empty default constructor as this class is meant to be used statically
    }

    /**
     * Hashes a password given a plain text password and salt
     * @param password password to hash
     * @param salt salt to increase hashing algorithm complexity
     * @return Hexidecimal string of hashed password
     * @throws ArithmeticException General exception for handling errors with retrieving hash algorithm or key spec errors
     */
    public static String hash(String password, String salt) throws ArithmeticException {
        try {
            char[] chars = password.toCharArray();

            PBEKeySpec spec = new PBEKeySpec(chars, salt.getBytes(), NUM_ITERATIONS, KEY_LENGTH);
            SecretKeyFactory skf = SecretKeyFactory.getInstance(HASH_ALGORITHM);
            byte[] hash = skf.generateSecret(spec).getEncoded();

            return toHex(hash);
        }
        catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new ArithmeticException(e.getMessage());
        }
    }

    /**
     * Responsible for generating a continuously unique salt for password hashing
     * @return Hexidecimal string of the salt
     * @throws ArithmeticException algorithm for randomizing salt was not found
     */
    public static String getSalt() throws ArithmeticException {
        try {
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            return toHex(salt);
        }
        catch(NoSuchAlgorithmException nsae){
            throw new ArithmeticException("Error creating a new salt.");
        }
    }

    /**
     * Converts a byte[] to a hexidecimal string
     * @param array byte[] to convert. Primarily for hash and salt conversion
     * @return Hexidecimal string of whatever byte[] is passed in
     */
    private static String toHex(byte[] array) {
        BigInteger bigInteger = new BigInteger(1, array);
        String hex = bigInteger.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0" +paddingLength+ "d", 0) + hex;
        } else {
            return hex;
        }
    }
}