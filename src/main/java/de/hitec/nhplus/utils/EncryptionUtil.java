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

/**
 * Die Klasse EncryptionUtil stellt zentrale Funktionen zur Ver- und
 * Entschlüsselung sensibler personenbezogener Daten bereit.
 *
 * <p>
 * Sie wurde zur Umsetzung der User Story „Schutz personenbezogener Daten“
 * eingeführt. Ziel ist es, sicherzustellen, dass vertrauliche Informationen
 * nicht im Klartext in der Datenbank gespeichert werden.
 * </p>
 *
 * <p>
 * Die Verschlüsselungslogik wurde bewusst in eine eigene Klasse ausgelagert,
 * um das Single-Responsibility-Prinzip (SRP) einzuhalten. Dadurch sind die
 * DAO-Klassen ausschließlich für den Datenbankzugriff verantwortlich,
 * während die EncryptionUtil die Verarbeitung sensibler Daten übernimmt.
 * </p>
 *
 * <p>
 * Die Klasse unterstützt sowohl das Verschlüsseln von Daten vor dem Speichern
 * als auch das Entschlüsseln nach dem Laden aus der Datenbank.
 * </p>
 *
 * @author Ben
 */
public class EncryptionUtil {
    /**
     * Der schlüssel ist derzeit zu Demonstrationszwecken statisch hinterlegt und
     * sollte in einer produktiven umgebung über ein sicheres Key-Management-System
     * bereitgestellt werden. Man könnte es bei einem so kleinen Projekt auch per Environmentvariable handhaben.
     */
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

    /**
     * Verschlüsselt einen Klartextwert für die Speicherung in der Datenbank.
     *
     * <p>
     *  Es wird der AES-Algorithmus im GCM-Modus verwendet, da dieser sowohl
     *  Vertraulichkeit als auch Integrität der Daten gewährleistet.
     *  Der GCM-Modus bietet zusätzlich Schutz vor Manipulationen der verschlüsselten Daten.
     *  (Als hilfe für die Implementierung des Algorithmus haben wir recherche betrieben)
     * </p>
     *
     * <p>
     * Die Methode wird vor dem Persistieren personenbezogener Daten aufgerufen,
     * um die Anforderungen der User Story zum Datenschutz umzusetzen.
     * </p>
     *
     * <p>
     * Durch die zentrale Bereitstellung der verschlüsselungslogik wird vermieden,
     * dass mehrere Klassen eigene Implementierungen enthalten.
     * </p>
     *
     * @param plainText der zu verschlüsselnde Klartext
     * @return Base64-kodierter verschlüsselter Text
     * @throws RuntimeException falls während der Verschlüsselung ein Fehler auftritt
     */
    public static String encrypt(String plainText){
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

    /**
     * Entschlüsselt einen zuvor gespeicherten Datenbankwert.
     *
     * <p>
     * Die Methode wird nach dem Laden von Daten aus der Datenbank verwendet,
     * damit die Anwendung ausschließlich mit lesbaren Informationen arbeitet.
     * </p>
     *
     * <p>
     * Sie bildet das Gegenstück zur Methode {@link #encrypt(String)}.
     * </p>
     *
     * @param encryptedText verschlüsselter Datenbankwert
     * @return entschlüsselter Klartext
     *
     * @throws Exception falls während der Entschlüsselung ein Fehler auftritt
     */
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