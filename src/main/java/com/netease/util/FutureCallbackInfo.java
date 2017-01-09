package com.netease.util;

import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;

import java.util.concurrent.CountDownLatch;

/**
 * Created by chenzihao on 2017/1/9.
 */
public class FutureCallbackInfo implements FutureCallback
{
	private CountDownLatch latch1;

	private String url;

	public CountDownLatch getLatch1()
	{
		return latch1;
	}

	public void setLatch1(CountDownLatch latch1)
	{
		this.latch1 = latch1;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	@Override
	public void completed(Object o)
	{
		final HttpResponse response2 = (HttpResponse) o;
		System.out.println(url + "request 2 start");
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
//		latch1.countDown();
		System.out.println(url + "request 2" + response2.getStatusLine() + Thread.currentThread());
	}

	public void failed(final Exception ex)
	{
		//					latch1.countDown();
		System.out.println("request 2" + "->" + ex);
	}

	public void cancelled()
	{
		//					latch1.countDown();
		System.out.println("request 2" + " cancelled");
	}
}
