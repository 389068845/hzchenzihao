package com.netease.other;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.httpasyncclient.FiberCloseableHttpAsyncClient;
import co.paralleluniverse.strands.SuspendableRunnable;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by chenzihao on 2017/1/10.
 */
public class FiberHttpAsyncClientTest
{


    @Test
    public void testAsync() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        final int concurrencyLevel = 20;
        // snippet client configuration
        final CloseableHttpAsyncClient client = FiberCloseableHttpAsyncClient.wrap(HttpAsyncClients.
                custom().
                setMaxConnPerRoute(concurrencyLevel).
                setMaxConnTotal(concurrencyLevel).
                build());
        client.start();
        // end of snippet
        new Fiber<Void>(new SuspendableRunnable() {
            @Override
            public void run() throws SuspendExecution, InterruptedException {
                try {
                    // snippet future calls
                    ArrayList<Future<HttpResponse>> futures = new ArrayList<>();
                    for (int i = 0; i < concurrencyLevel; i++)
                        futures.add(client.execute(new HttpGet("https://epay.163.com/ewallet_app_api/v1/device_activate.htm"), null));
                    for (Future<HttpResponse> future : futures)
                        System.out.println(EntityUtils.toString(future.get().getEntity()));

                    // end of snippet
                } catch (ExecutionException | IOException | ParseException ex) {

                }
            }
        }).start().join(5000, TimeUnit.MILLISECONDS);
        client.close();
    }

    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            try (PrintWriter out = resp.getWriter()) {
                Thread.sleep(500);
                out.print("testGet");
            } catch (InterruptedException ex) {
            }
        }
    }
}
