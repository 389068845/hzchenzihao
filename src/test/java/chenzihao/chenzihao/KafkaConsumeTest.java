/*
 * FileName: KafkaConsumeTest.java
 * Author:   hzchenzihao
 * Date:     2016年5月31日 下午5:38:58
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package chenzihao.chenzihao;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

public class KafkaConsumeTest extends Thread
{
	private final ConsumerConnector consumer;
	private final String topic;

	public static void main(String[] args)
	{
		KafkaConsumeTest consumerThread = new KafkaConsumeTest("mykafka");
		consumerThread.start();
	}

	public KafkaConsumeTest(String topic) {    
                consumer =kafka.consumer.Consumer    
                        .createJavaConsumerConnector(createConsumerConfig());    
                this.topic =topic;    
            }

	private static ConsumerConfig createConsumerConfig()
	{
		Properties props = new Properties();
		props.put("zookeeper.connect", "10.XX.XX.XX:2181,10.XX.XX.XX:2181,10.XX.XX.XX:2181");
		props.put("group.id", "0");
		props.put("zookeeper.session.timeout.ms", "10000");
		return new ConsumerConfig(props);
	}
	
public void init(){
	
}

	public void run()
	{
		Map<String, Integer> topickMap = new HashMap<String, Integer>();
		topickMap.put(topic, 1);
		Map<String, List<KafkaStream<byte[], byte[]>>> streamMap = consumer.createMessageStreams(topickMap);
		KafkaStream<byte[], byte[]> stream = streamMap.get(topic).get(0);
		ConsumerIterator<byte[], byte[]> it = stream.iterator();
		System.out.println("*********Results********");
		while (true)
		{
			if (it.hasNext())
			{

				System.err.println("get data:" + new String(it.next().message()));
			}
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
