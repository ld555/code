package me.chuang6.jz.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class AESUtils {
	private static final byte[] KEY = "KjBy*7$cUi01@3?.".getBytes();

	public static String encrypt(String src) {

		try {
			// 生成key
//			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//			keyGenerator.init(128);
//			 keyGenerator.init(new SecureRandom());
//			SecretKey generateKey = keyGenerator.generateKey();
//			byte[] bytesKey = generateKey.getEncoded();

			// 转换KEY
			Key key = new SecretKeySpec(KEY, "AES");

			// 加密
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] doFinal = cipher.doFinal(src.getBytes());
			return Base64.encodeBase64String(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String decrypt(String src) {

		try {
			// 生成key
//			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//			keyGenerator.init(128);
//			// keyGenerator.init(new SecureRandom());
//			SecretKey generateKey = keyGenerator.generateKey();
//			byte[] bytesKey = generateKey.getEncoded();

			// 转换KEY
			Key key = new SecretKeySpec(KEY, "AES");

			// 加密
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] doFinal = cipher.doFinal(Base64.decodeBase64(src));
			return new String(doFinal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
