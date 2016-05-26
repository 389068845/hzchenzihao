/*
 * FileName: SignUtil.java
 * Author:   hzchenzihao
 * Date:     2016年5月26日 下午2:34:13
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.netease.util;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class SignUtil
{
		
	public static boolean doCheck(String content, String sign, String publicKey,String encode)  
    {  
        try   
        {  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            byte[] encodedKey = Base64.decodeBase64(publicKey);  
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));  
  
          
            java.security.Signature signature = java.security.Signature  
            .getInstance("SHA1withRSA");  
          
            signature.initVerify(pubKey);  
            signature.update( content.getBytes(encode) );  
          
            boolean bverify = signature.verify( Base64.decodeBase64(sign) );  
            return bverify;  
              
        }   
        catch (Exception e)   
        {  
            e.printStackTrace();  
        }  
          
        return false;  
    }  
	public static void main(String[] args)
	{
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnoMPlJZHUImcQjajQdTbk82goHBQ8GNrzZFeFRbLaHc8NT3a5q3deT/WVnmyxf1TmKZII+z06ideTItNRKAK3Ej7YJe0XrilcKopAmRKwuSMi2Td1QTLSQisHJ1q/IQcnXL4YtL/6O0nVvzufM0HvRQVr3wADdJ28ApZwb1b4FwIDAQAB";
		System.out.println(doCheck("eyJhbW91bnQiOiIyNTM0MCIsInBsYXRmb3JtSWQiOiIyMDE0MTExMTIwUFQxNTMzNDUzNyJ9","Ad1+bRvtC5urPXS6PLAS7ZaUKmnyHBTQenBMwXwuNuB89dnmuvbOMYbmSNNHjRgdHj4Shq5s3AuEtnWK8NFoTEaYQu3u8Y/Og9JpE13QaSIcoFYbXSACtxZbhb1cvRxuXhz9rUbPUOhakYj+q5hPfw/eh/AqMg5FrGgqU98TdaA=",publicKey,"UTF-8"));
	}
}
