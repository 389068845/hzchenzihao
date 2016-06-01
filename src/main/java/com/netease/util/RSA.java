package com.netease.util;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;

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
	
	
	   public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)  
	            throws Exception {  
	        try {  
	            byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(privateKeyStr);  
	            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);  
	            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
	            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);  
	        } catch (NoSuchAlgorithmException e) {  
	            throw new Exception("无此算法");  
	        } catch (InvalidKeySpecException e) {  
	            throw new Exception("私钥非法");  
	        } catch (NullPointerException e) {  
	            throw new Exception("私钥数据为空");  
	        }  
	    } 
	   
	   private void en(){
		  
	   }
	 /** 
     * 私钥解密 
     *  
     * @param data 
     * @param privateKey 
     * @return 
     * @throws Exception 
     */  
    public static String decryptByPrivateKey(String data, String privateKeyStr)  
            throws Exception {  
    	byte[] buffer = org.apache.commons.codec.binary.Base64.decodeBase64(privateKeyStr);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
		RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, privateKey);  
        //模长  
        int key_len = privateKey.getModulus().bitLength() / 8;  
        byte[] bytes = data.getBytes();  
        byte[] bcd = ASCII_To_BCD(bytes, bytes.length);  
        System.err.println(bcd.length);  
        //如果密文长度大于模长则要分组解密  
        String ming = "";  
        byte[][] arrays = splitArray(bcd, key_len);  
        for(byte[] arr : arrays){  
            ming += new String(cipher.doFinal(arr));  
        }  
        return ming;  
    }  
    
    /** 
     *拆分数组  
     */  
    public static byte[][] splitArray(byte[] data,int len){  
        int x = data.length / len;  
        int y = data.length % len;  
        int z = 0;  
        if(y!=0){  
            z = 1;  
        }  
        byte[][] arrays = new byte[x+z][];  
        byte[] arr;  
        for(int i=0; i<x+z; i++){  
            arr = new byte[len];  
            if(i==x+z-1 && y!=0){  
                System.arraycopy(data, i*len, arr, 0, y);  
            }else{  
                System.arraycopy(data, i*len, arr, 0, len);  
            }  
            arrays[i] = arr;  
        }  
        return arrays;  
    }  
  
    /** 
     * ASCII码转BCD码 
     *  
     */  
    public static byte[] ASCII_To_BCD(byte[] ascii, int asc_len) {  
        byte[] bcd = new byte[asc_len / 2];  
        int j = 0;  
        for (int i = 0; i < (asc_len + 1) / 2; i++) {  
            bcd[i] = asc_to_bcd(ascii[j++]);  
            bcd[i] = (byte) (((j >= asc_len) ? 0x00 : asc_to_bcd(ascii[j++])) + (bcd[i] << 4));  
        }  
        return bcd;  
    }  
    public static byte asc_to_bcd(byte asc) {  
        byte bcd;  
  
        if ((asc >= '0') && (asc <= '9'))  
            bcd = (byte) (asc - '0');  
        else if ((asc >= 'A') && (asc <= 'F'))  
            bcd = (byte) (asc - 'A' + 10);  
        else if ((asc >= 'a') && (asc <= 'f'))  
            bcd = (byte) (asc - 'a' + 10);  
        else  
            bcd = (byte) (asc - 48);  
        return bcd;  
    }  
    /** 
     * BCD转字符串 
     */  
    public static String bcd2Str(byte[] bytes) {  
        char temp[] = new char[bytes.length * 2], val;  
  
        for (int i = 0; i < bytes.length; i++) {  
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);  
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
  
            val = (char) (bytes[i] & 0x0f);  
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');  
        }  
        return new String(temp);  
    }  
    /** 
     * 拆分字符串 
     */  
    public static String[] splitString(String string, int len) {  
        int x = string.length() / len;  
        int y = string.length() % len;  
        int z = 0;  
        if (y != 0) {  
            z = 1;  
        }  
        String[] strings = new String[x + z];  
        String str = "";  
        for (int i=0; i<x+z; i++) {  
            if (i==x+z-1 && y!=0) {  
                str = string.substring(i*len, i*len+y);  
            }else{  
                str = string.substring(i*len, i*len+len);  
            }  
            strings[i] = str;  
        }  
        return strings;  
    } 

    public static void main(String[] args)
	{
		System.out.println(encrypt("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbIq0lpTe7wD7lBgf81e9rsFsrnYH2opDnNkShv5mFZDX8Vuadm/LtV5cX16ymKAqJhnXwV011FozwYp9DIAZWwHEou3C6r0JdFbJaxCPp7gftf2B/ilf+JWtZQW38ZVED/xie7B2HeOz6bA3B2FCDe7dmnSNjpAVClrlM/SJwbwIDAQAB","nV6x+kFCODZCQUNFLTk3OUQtNDk0Qi05RTU3LUUzOTY1MDQ1QTA1NA=="));
		System.out.println();
	}
	/**
	 * RSA 加密
	 *
	 * @param publicKeyStr  公钥
	 * @param plainTextData 被加密字符串
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String publicKeyStr, String plainTextData)
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