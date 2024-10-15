package com.bank.app.api_gateway.Util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class PasswordUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "E7P/iFOV3eLtlcpPQQVu5T0LVmm0Sxl2GJwHbNN9Eqg="; // Must match the key used in the frontend
    private static final String IV = "1234567890123456";
    public static String decrypt(String encryptedPassword) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes()); // Use the same IV

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding"); // Match the mode and padding used in the front end
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
        return new String(decryptedBytes);
    }
}
