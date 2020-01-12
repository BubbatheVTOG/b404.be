package b404.utility.security;

public class PasswordEncryption {
    public static String encrypt(String password){
        return password;
        /*try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            byte[] messageDigest = md.digest(password.getBytes());

            BigInteger num = new BigInteger(1, messageDigest);

            String hashedPassword = num.toString(16);

            while (hashedPassword.length() < 32) {
                hashedPassword = "0" + hashedPassword;
            }
            return hashedPassword;
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException(nsae);
        }*/
    }
}
