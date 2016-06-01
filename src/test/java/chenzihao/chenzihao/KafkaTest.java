/*
 * FileName: KafkaTest.java
 * Author:   hzchenzihao
 * Date:     2016年5月31日 下午5:35:10
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package chenzihao.chenzihao;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class KafkaTest
{
	private static KafkaProducer<Object, Object> tmpProducer;

	//			kafka.bootstrap.servers=10.165.124.120:29092
	//			kafka.schema.registry.url=http://10.165.124.120:28088
	public static void main(String[] args)
	{
		System.out.println("------start init-------");
		String topic = "behavior.epay.PayOrderLite";
		Properties prop = new Properties();
		prop.put("bootstrap.servers", "10.165.124.120:29092");
		prop.put("value.serializer", KafkaAvroSerializer.class);
		prop.put("key.serializer", io.confluent.kafka.serializers.KafkaAvroSerializer.class);
		prop.put("metadata.fetch.timeout.ms", 5000);
		prop.put("schema.registry.url", "http://10.165.124.120:28088");
		tmpProducer = new KafkaProducer<Object, Object>(prop);
		try
		{
			tmpProducer.partitionsFor(topic);
		}
		catch (Exception e)
		{
			System.out.println("------exception-------");
		}

		try
		{
			String msg = "kafka test";
			System.out.println("------start-------");
			ProducerRecord<Object, Object> record = new ProducerRecord<Object, Object>(topic, msg);
			tmpProducer.send(record, new Callback() {

				public void onCompletion(RecordMetadata metadata, Exception e)
				{
					if (e != null)
					{
						
					}
					System.out.println("------send------");
				}
			});
		}
		catch (Exception e)
		{
			System.out.println("------end");
		}
	}

}
