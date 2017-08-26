/*
 * FileName: HttpUtil.java
 * Author:   hzchenzihao
 * Date:     2016年5月20日 上午11:59:45
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.netease.util;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HttpUtil
{
	public static void main(String[] args)
	{
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("valueXss",
//				"pc&#95;identify&quot;&gt;&lt;img&#32;src&#61;x&#32;onerror&#61;alert&#40;1&#41;&#47;&#47;&&");
//		params.put("valueXss2", "&");
//		System.out.println("--------------------------------------------------------------");
//		System.out.println(sendPost("http://127.0.0.1/quickpay/apply.htm", params));
//		System.out.println("--------------------------------------------------------------");
		char s = 'a';
		System.out.println(s > 0x7F);
		
		
	}

	public static String sendPost(String url, Map<String, String> params)
	{

		BufferedReader reader = null;
		HttpClient httpClient = new DefaultHttpClient();
		try
		{
			HttpGet httpPost = new HttpGet(url);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 请求超时 
			nameValuePairs.add(new BasicNameValuePair(CoreConnectionPNames.CONNECTION_TIMEOUT, "50000"));
			// 读取超时
			nameValuePairs.add(new BasicNameValuePair(CoreConnectionPNames.SO_TIMEOUT, "50000"));
//			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "GBK"));
			HttpResponse response = httpClient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200)
			{
				reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String str = reader.readLine();
				StringBuilder sb = new StringBuilder();
				while (str != null)
				{
					sb.append(str);
					str = reader.readLine();
				}
				return sb.toString();
			}
			else
			{
				return "";
			}
		}
		catch (UnsupportedEncodingException e)
		{

		}
		catch (ClientProtocolException e)
		{

		}
		catch (IOException e)
		{

		}
		finally
		{
			if (null != reader)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{

				}
			}
			httpClient.getConnectionManager().closeIdleConnections(0, TimeUnit.MILLISECONDS);
		}
		return "";

	}

	public static String sendPost2(String url, Map<String, String> params)
	{
		StringBuilder postParams = new StringBuilder();
		for (Map.Entry<String, String> m : params.entrySet())
		{
			postParams.append("&").append(m.getKey()).append("=").append(m.getValue());
		}

		URL webURL;
		OutputStream httpOutputStream = null;
		BufferedReader httpBufferedReader = null;
		try
		{
			webURL = new URL(url);
			HttpURLConnection httpURLConnection = (HttpURLConnection) webURL.openConnection();
			httpURLConnection.setDoOutput(true);// 打开写入属性  
			httpURLConnection.setDoInput(true);// 打开读取属性  
			httpURLConnection.setRequestMethod("GET");// 设置提交方法
			httpURLConnection.setConnectTimeout(50000);// 连接超时时间  
			httpURLConnection.setReadTimeout(50000);
			httpURLConnection.connect();
			httpOutputStream = httpURLConnection.getOutputStream();
			httpOutputStream.write(postParams.toString().getBytes());
			httpBufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String result = "";
			while ((result = httpBufferedReader.readLine()) != null)
			{
				sb.append(result);
			}
			return new String(sb.toString());
		}
		catch (Exception e)
		{

		}
		finally
		{
			if (null != httpBufferedReader)
			{
				try
				{
					httpBufferedReader.close();
				}
				catch (IOException e)
				{

				}
			}
			if (null != httpOutputStream)
			{
				try
				{
					httpOutputStream.close();
				}
				catch (IOException e)
				{

				}
			}
		}
		return null;

	}
	
	private String stripXSS(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);

            // Avoid null characters
            value = value.replaceAll("", "");

            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid anything in a src='...' type of e­xpression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid eval(...) e­xpressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid e­xpression(...) e­xpressions
            scriptPattern = Pattern.compile("e­xpression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid javascript:... e­xpressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid vbscript:... e­xpressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");

            // Avoid onload= e­xpressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;
    }
}
