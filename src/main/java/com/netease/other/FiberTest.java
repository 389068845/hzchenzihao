package com.netease.other;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.strands.SuspendableRunnable;
import co.paralleluniverse.strands.channels.Channel;
import co.paralleluniverse.strands.channels.Channels;
import co.paralleluniverse.strands.concurrent.CountDownLatch;
import com.netease.util.HttpUtil;
import jsr166e.LongAdder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by chenzihao on 2017/1/7.
 */
public class FiberTest
{
	static void m1() throws InterruptedException, SuspendExecution
	{
		String m = "m1";

		m = m2();
	}

	static String m2() throws SuspendExecution, InterruptedException
	{
		String m = m3();
//		Strand.sleep(1000);
		return m;
	}

	//or define in META-INF/suspendables
	//	@Suspendable
	static String m3()
	{
		List l = Stream.of(1, 2, 3).filter(i -> i % 2 == 0).collect(Collectors.toList());
		return l.toString();
	}

	static public void main3(String[] args) throws ExecutionException, InterruptedException
	{
		int count = 1000;
		testThreadpool(count);
		testFiber(count);

	}

	static public void main(String[] args) throws ExecutionException, InterruptedException
	{
		int count = 1000;
		testFiberPost(2);

	}

	static void testThreadpool(int count) throws InterruptedException
	{
		final CountDownLatch latch = new CountDownLatch(count);
		ExecutorService es = Executors.newFixedThreadPool(200);
		LongAdder latency = new LongAdder();
		long t = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			es.submit(() -> {
				long start = System.currentTimeMillis();
				try
				{
					m1();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				catch (SuspendExecution suspendExecution)
				{
					suspendExecution.printStackTrace();
				}
				start = System.currentTimeMillis() - start;
				latency.add(start);
				latch.countDown();
			});
		}
		latch.await();
		t = System.currentTimeMillis() - t;
		long l = latency.longValue() / count;
		System.out.println("thread pool took: " + t + ", latency: " + l + " ms");
		es.shutdownNow();
	}

	static void testFiber(int count) throws InterruptedException
	{
		final CountDownLatch latch = new CountDownLatch(count);
		LongAdder latency = new LongAdder();
		long t = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			new Fiber<Void>("Caller", new SuspendableRunnable() {
				@Override
				public void run() throws SuspendExecution, InterruptedException
				{
					long start = System.currentTimeMillis();
					m1();
					start = System.currentTimeMillis() - start;
					latency.add(start);
					latch.countDown();
				}
			}).start();
		}
		latch.await();
		t = System.currentTimeMillis() - t;
		long l = latency.longValue() / count;
		System.out.println("fiber took: " + t + ", latency: " + l + " ms");
	}



	private static void printer(Channel<Integer> in) throws SuspendExecution, InterruptedException
	{
		Integer v;
		while ((v = in.receive()) != null)
		{
			System.out.println(v);
		}
	}

	public static void main2(String[] args) throws ExecutionException, InterruptedException, SuspendExecution
	{
		//定义两个Channel
		Channel<Integer> naturals = Channels.newChannel(-1);
		Channel<Integer> squares = Channels.newChannel(-1);
		Channel<Integer> squares2 = Channels.newChannel(-1);
		Channel<Integer> naturals2 = Channels.newChannel(-1);

		long t = System.currentTimeMillis();
		ExecutorService es = Executors.newFixedThreadPool(200);
		es.submit(() -> {
			for (int i = 0; i < 10000; i++)
				try
				{
					naturals.send(i);
				}
				catch (SuspendExecution suspendExecution)
				{
					suspendExecution.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			naturals.close();
		});

		es.submit(() -> {
			Integer v;
			try
			{
				while ((v = naturals.receive()) != null)
					squares.send(v * v);
			}
			catch (SuspendExecution suspendExecution)
			{
				suspendExecution.printStackTrace();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			squares.close();
		});
		//		printer(squares);
		System.out.println("耗时:" + (System.currentTimeMillis() - t));

		long t2 = System.currentTimeMillis();
		//运行两个Fiber实现.
		new Fiber(() -> {
			for (int i = 0; i < 10000; i++)
				naturals2.send(i);
			naturals2.close();
		}).start();

		new Fiber(() -> {
			Integer v;
			while ((v = naturals2.receive()) != null)
				squares2.send(v * v);
			squares2.close();
		}).start();

		//		printer(squares2);
		System.out.println("耗时2:" + (System.currentTimeMillis() - t2));
	}


	@Suspendable
	static void testFiberPost(int count) throws InterruptedException
	{

		final CountDownLatch latch = new CountDownLatch(count);
		LongAdder latency = new LongAdder();
		long t = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			new Fiber<Void>("Caller", new SuspendableRunnable() {
				@Override
				public void run() throws SuspendExecution, InterruptedException
				{
					long start = System.currentTimeMillis();
					Map<String, String> params = new HashMap<String, String>();
					params.put("msg","123");
					HttpUtil.sendPost("https://epay.163.com/ewallet_app_api/v1/device_activate.htm",params);
					start = System.currentTimeMillis() - start;
					latency.add(start);
					latch.countDown();
				}
			}).start();
		}
		latch.await();
		t = System.currentTimeMillis() - t;
		long l = latency.longValue() / count;
		System.out.println("fiber took: " + t + ", latency: " + l + " ms");
	}
}
