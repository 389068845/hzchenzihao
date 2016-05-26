package com.netease.util;
import java.security.Key;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;

/*
 * FileName: RSA.java
 * Author:   hzchenzihao
 * Date:     2016年5月17日 下午7:49:28
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class RSA
{
	public static void main(String[] args)
	{
		String token = new RSA().encode(
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCNK/aN0bWweKdu1qw0J7TCUke6kEnvzBYO8Vr8UtFXO6rSWMA6v5y53wF1t6lSyzr549jE/Rp7LrWJ6X6HCrYHOJoecEzUojbAWEtApSc7jSkZHfgfMOx4Z8zRCM/JmvoiE9HlOAnGlxt4GDCDPHKULpVl0BmHx8KJl5ClKV2wQIDAQAB",
				"1234");
		System.out.println(token);
		System.out.println(org.apache.commons.codec.binary.Base64.decodeBase64("1234"));
	}

	/**
	 * RSA 加密
	 *
	 * @param publicKeyStr  公钥
	 * @param plainTextData 被加密字符串
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String publicKeyStr, String plainTextData)
	{

		try
		{
			byte[] buffer = Base64.decodeBase64(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			Cipher cipher = Cipher.getInstance("RSA/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData.getBytes("UTF-8"));
			String temp = new String(output);

			return new String(Base64.encodeBase64(output), "UTF-8");
		}
		catch (Exception e)
		{

			e.printStackTrace();
			return "";
		}
	}

	public static byte[] encryptByHexPublicKey(byte[] data, String publicHexKey) throws Exception
	{
		byte[] keyBytes = hexStrToBytes(publicHexKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory fac = KeyFactory.getInstance("RSA");
		Key key = fac.generatePublic(keySpec);
		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(data);
	}

	@SuppressWarnings("static-access")
	public static String encode(String publicKeyStr, String plainTextData)
	{

		try
		{
			byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(publicKeyStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] output = cipher.doFinal(plainTextData.getBytes("UTF-8"));
			return new String(new org.apache.commons.codec.binary.Base64().encodeBase64(output), "UTF-8");
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
		return "";
	}

	public static final byte[] hexStrToBytes(String s)
	{
		byte[] bytes;

		bytes = new byte[s.length() / 2];

		for (int i = 0; i < bytes.length; i++)
		{
			bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
		}

		return bytes;
	}
}