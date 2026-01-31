import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;

public class SecurityUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final int SALT_LENGTH_BYTE = 16;
    private static final int ITERATION_COUNT = 600000;
    private static final int KEY_LENGTH_BIT = 256;

    public static void save(String content, char[] password, java.io.File file) throws Exception {
        // 1. Generate Random Salt and IV
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        random.nextBytes(salt);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        random.nextBytes(iv);

        // 2. Derive Key
        SecretKey secretKey = deriveKey(password, salt);

        // 3. Encrypt
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] cipherText = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));

        // 4. Pack Data: [SALT(16)] + [IV(12)] + [CIPHERTEXT]
        ByteBuffer byteBuffer = ByteBuffer.allocate(salt.length + iv.length + cipherText.length);
        byteBuffer.put(salt);
        byteBuffer.put(iv);
        byteBuffer.put(cipherText);

        Files.write(file.toPath(), byteBuffer.array());
    }

    public static String load(char[] password, java.io.File file) throws Exception {
        if (!file.exists()) {
            throw new Exception("No save file found.");
        }

        byte[] fileContent = Files.readAllBytes(file.toPath());
        ByteBuffer byteBuffer = ByteBuffer.wrap(fileContent);

        // 1. Extract Salt
        if (byteBuffer.remaining() < SALT_LENGTH_BYTE + IV_LENGTH_BYTE) {
            throw new Exception("File corrupted");
        }
        byte[] salt = new byte[SALT_LENGTH_BYTE];
        byteBuffer.get(salt);

        // 2. Extract IV
        byte[] iv = new byte[IV_LENGTH_BYTE];
        byteBuffer.get(iv);

        // 3. Extract CipherText
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        // 4. Derive Key
        SecretKey secretKey = deriveKey(password, salt);

        // 5. Decrypt
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

        byte[] plainTextBytes = cipher.doFinal(cipherText);
        return new String(plainTextBytes, StandardCharsets.UTF_8);
    }

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH_BIT);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            SecretKey tmp = factory.generateSecret(spec);
            return new SecretKeySpec(tmp.getEncoded(), "AES");
        } finally {
            spec.clearPassword();
        }
    }

    public static String checkStrength(String password) {
        if (password == null || password.length() < 8)
            return "Weak (Too short)";
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");

        int score = 0;
        if (hasUpper)
            score++;
        if (hasLower)
            score++;
        if (hasDigit)
            score++;
        if (hasSpecial)
            score++;

        if (score < 2)
            return "Weak";
        if (score == 2 || score == 3)
            return "Medium";
        return "Strong";
    }
}

