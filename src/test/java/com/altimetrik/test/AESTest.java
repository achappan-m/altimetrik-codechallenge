package com.altimetrik.test;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
class AESTest {

	@Test
	void test() {
		String userName="achappan";
		String password="altimetrikcoding";
		String secret = "altimetrikcoding";
		
		String encryptedString= encryptAes(userName, password, secret);
		System.out.println(" EncryptedString ::: "+ encryptedString);
		String decryptedString= decrypt(encryptedString, password, secret);
		System.out.println(" decryptedString ::: " + decryptedString);
	}

	 String encryptAes(String userName, String password, String secret) {
		 
		try {
			IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
			SecretKeySpec secretkeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, secretkeySpec, iv);

			byte[] encrypted = cipher.doFinal(userName.getBytes());
			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "Exception in Encryption";
	}
	
	 
	 String decrypt(String encrypted, String username, String secret) {
			try {
				IvParameterSpec iv = new IvParameterSpec(username.getBytes("UTF-8"));
				SecretKeySpec secretkeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "AES");

				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
				cipher.init(Cipher.DECRYPT_MODE, secretkeySpec, iv);
				byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

				return new String(original);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			return null;
		}
}
