package at.fhtw.mrp.service;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class HashUtil {

    public static String generateHashedPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] hash = f.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getEncoder();
            return enc.encodeToString(hash) + ":" + enc.encodeToString(salt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Es gibt einen Fehler beim Hashen des Passworts", e);
        }
    }

    public static boolean checkHashedPassword(String hashedPassword, String passwordToCheck) {
        try {
            Base64.Decoder dec = Base64.getDecoder();
            String[] split = hashedPassword.split(":");
            byte[] hash = dec.decode(split[0]);
            byte[] salt = dec.decode(split[1]);
            KeySpec spec = new PBEKeySpec(passwordToCheck.toCharArray(), salt, 65536, 128);
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            byte[] newHash = f.generateSecret(spec).getEncoded();
            return Arrays.equals(newHash, hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Es gibt einen Fehler beim Prüfen des Passworts", e);
        }
    }
}
