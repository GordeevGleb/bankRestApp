package org.example.bank_app.util;

public interface Encryptor {

    String encrypt(String data);

    String decrypt(String data);

    String mask(String decryptedCardNumber);
}
