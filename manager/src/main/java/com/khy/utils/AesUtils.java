package com.khy.utils;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.alipay.api.internal.util.codec.Base64;
import com.khy.common.Constants;

public class AesUtils {
	public static void main(String[] args) throws Exception {
		String encrypt = encrypt("qNbuDK656ziShag3287ujA==","你好啊傻逼呢","UTF-8");
		System.out.println(encrypt);
		String decrypt = decrypt(encrypt, "qNbuDK656ziShag3287ujA==", "UTF-8");
		System.out.println(decrypt);
	}
	
	public static String encrypt(String key,String content,String charset) throws Exception {
		String fullAlg = Constants.AES_FULLALG;
		Cipher cipher = Cipher.getInstance(fullAlg);
		IvParameterSpec iv = new IvParameterSpec(initIv(fullAlg));
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Base64.decodeBase64(key.getBytes()), "AES"), iv);
		byte[] encryptBytes = cipher.doFinal(content.getBytes(charset));
		return new String(Base64.encodeBase64(encryptBytes));
	}

	/**
	 * 初始向量的方法, 全部为0. 这里的写法适合于其它算法,针对AES算法的话,IV值一定是128位的(16字节).
	 *
	 * @param fullAlg
	 * @return
	 * @throws GeneralSecurityException
	 */
	private static byte[] initIv(String fullAlg) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance(fullAlg);
		int blockSize = cipher.getBlockSize();
		byte[] iv = new byte[blockSize];
		for (int i = 0; i < blockSize; ++i) {
			iv[i] = 0;
		}
		return iv;
	}
	
	/**
     * 
     * @param content 密文
     * @param key aes密钥
     * @param charset 字符集
     * @return 原文
     * @throws EncryptException
     */
    public static String decrypt(String content, String key, String charset) throws Exception {
        //反序列化AES密钥
        SecretKeySpec keySpec = new SecretKeySpec(Base64.decodeBase64(key.getBytes()), "AES");
        //128bit全零的IV向量
        byte[] iv = new byte[16];
        for (int i = 0; i < iv.length; i++) {
            iv[i] = 0;
        }
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        String fullAlg = Constants.AES_FULLALG;
        //初始化加密器并加密
        Cipher deCipher = Cipher.getInstance(fullAlg);
        deCipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);
        byte[] encryptedBytes = Base64.decodeBase64(content.getBytes());
        byte[] bytes = deCipher.doFinal(encryptedBytes);
        return new String(bytes);
    }
}
