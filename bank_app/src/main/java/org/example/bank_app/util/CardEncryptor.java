package org.example.bank_app.util;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class CardEncryptor implements Encryptor {

    @Value("${encryption.card-secret}")
    private String secret;

    private SecretKeySpec secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        byte[] key = new byte[16];
        System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 16));
        this.secretKey = new SecretKeySpec(key, "AES");
    }

    @Override
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования карты", e);
        }
    }

    @Override
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка расшифровки карты", e);
        }
    }

    @Override
    public String mask(String decryptedCardNumber) {
        int visible = 4;
        if (decryptedCardNumber == null || decryptedCardNumber.length() <= visible)
            return "*".repeat(decryptedCardNumber.length());
        return "*".repeat(decryptedCardNumber.length() - visible) +
                decryptedCardNumber.substring(decryptedCardNumber.length() - visible);
    }
}

