package com.rhwayfun.sso.common;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

/**
 * DES工具
 * @author preach
 *
 */
public class DES {

	/**
	 * 加密
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String source, String key) throws Exception {
		
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, getKey(key));
		byte[] dest = cipher.doFinal(source.getBytes("utf-8"));
		
		return Base64.getEncoder().encodeToString(dest);
	}
	
	/**
	 * 解密
	 * @param source
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String source, String key) throws Exception {
		
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, getKey(key));
		byte[] dest = cipher.doFinal(Base64.getDecoder().decode(source));
		return new String(dest, "utf-8");
	}
	
	private static Key getKey(String key) throws Exception {
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(new SecureRandom(key.getBytes("utf-8")));
		Key skey = keyGenerator.generateKey();
		return skey;
	}
	
	public static void main(String[] args) throws Exception {
		
		String src = "哈罗hello,test，大厦";
		String key = "a--2++sdf";
		
		String dest = DES.encrypt(src, key);
		System.out.println(dest);
		
		System.out.println(DES.decrypt(dest, "a--2++sdf"));
		
	}
}
