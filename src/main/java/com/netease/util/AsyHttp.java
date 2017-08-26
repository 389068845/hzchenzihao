package com.netease.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

public class AsyHttp
{
	public static void main(final String[] args) throws Exception
	{
		CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
		try
		{
			// Start the client
			httpclient.start();

			// Execute request
			final HttpGet request1 = new HttpGet("http://www.apache.org/");
			Future<HttpResponse> future = httpclient.execute(request1, null);
			// and wait until a response is received
			HttpResponse response1 = future.get();
			System.out.println("request 1" + request1.getRequestLine() + "->" + response1.getStatusLine()
					+ Thread.currentThread());

			// One most likely would want to use a callback for operation result
			final CountDownLatch latch1 = new CountDownLatch(1);
			final HttpGet request2 = new HttpGet("http://epay.163.com/ewallet_app_api/v1/device_activate.htm");
			FutureCallbackInfo info = new FutureCallbackInfo();
			info.setUrl("ww.b");
			info.setLatch1(latch1);
			Future<HttpResponse> respon = httpclient.execute(request2, (FutureCallback<HttpResponse>) info);


//						latch1.await();
			final CountDownLatch latch2 = new CountDownLatch(1);
			final HttpGet request3 = new HttpGet("http://www.apache.org/");
			HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request3);
			AsyncCharConsumer<HttpResponse> consumer3 = new AsyncCharConsumer<HttpResponse>() {

				HttpResponse response;

				@Override
				protected void onResponseReceived(final HttpResponse response)
				{
					this.response = response;
				}

				@Override
				protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException
				{
					// Do something useful
				}

				@Override
				protected void releaseResources()
				{
				}

				@Override
				protected HttpResponse buildResult(final HttpContext context)
				{
					return this.response;
				}

			};
			httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

				public void completed(final HttpResponse response3)
				{
					latch2.countDown();
					System.out.println("request 3" + request3.getRequestLine() + "->" + response3.getStatusLine());
				}

				public void failed(final Exception ex)
				{
					latch2.countDown();
					System.out.println("request 3" + request3.getRequestLine() + "->" + ex);
				}

				public void cancelled()
				{
					latch2.countDown();
					System.out.println("request 3" + request3.getRequestLine() + " cancelled");
				}

			});
			latch2.await();

			System.out.println("request 2 excuteded:"  + EntityUtils.toString(respon.get().getEntity()));

		}
		finally
		{
			httpclient.close();
		}

	}

}