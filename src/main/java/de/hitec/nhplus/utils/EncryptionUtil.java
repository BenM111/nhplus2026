package de.hitec.nhplus.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class EncryptionUtil {

    private static final String SECRET_KEY =
            "12345678901234567890123456789012";

    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;

    private static SecretKey getKey() {
        return new SecretKeySpec(
                SECRET_KEY.getBytes(StandardCharsets.UTF_8),
                "AES"
        );
    }

    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        try {
            byte[] iv = new byte[IV_LENGTH];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    getKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            byte[] encryptedBytes =
                    cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + encryptedBytes.length];

            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(
                    encryptedBytes,
                    0,
                    combined,
                    iv.length,
                    encryptedBytes.length
            );

            return Base64.getEncoder().encodeToString(combined);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Verschlüsseln", e);
        }
    }

    public static String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }

        try {
            byte[] combined =
                    Base64.getDecoder().decode(encryptedText);

            byte[] iv =
                    Arrays.copyOfRange(combined, 0, IV_LENGTH);

            byte[] encryptedBytes =
                    Arrays.copyOfRange(
                            combined,
                            IV_LENGTH,
                            combined.length
                    );

            Cipher cipher =
                    Cipher.getInstance("AES/GCM/NoPadding");

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    getKey(),
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv)
            );

            byte[] decryptedBytes =
                    cipher.doFinal(encryptedBytes);

            return new String(
                    decryptedBytes,
                    StandardCharsets.UTF_8
            );

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Entschlüsseln", e);
        }
    }
}