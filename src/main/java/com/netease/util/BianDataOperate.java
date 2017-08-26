package com.netease.util;

import com.alibaba.fastjson.JSON;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by chenzihao on 2017/8/18.
 */
public class BianDataOperate
{
	public static void main(String[] args) throws Exception
	{
		String url = "https://boss.otosaas.com/api/bss/v1/app/157/overview?";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		cal.add(Calendar.DATE, -7);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);

		List<List<BusinessData>> excleData = new ArrayList<>();
		int channelNo = 1;

		for (int i = 0; i < 7; i++)
		{
			String queryDate = df.format(cal.getTime());
			String totalAmountOneDay = "0";
			float totalAmount = 0f;
			List<BusinessData> businessDataList = new ArrayList<>();
			for (Channel channel : onlineChannels)
			{
				String finalUrl = url + "start_date=" + queryDate + "&end_date=" + queryDate + "&channel="
						+ channel.getChannelName();
				float Speed = (float) (channelNo) / (float) (onlineChannels.size() * 7) * 100;
				System.out.println("生成数据中，进度:" + new BigDecimal(Speed).setScale(0, BigDecimal.ROUND_DOWN) + "%");
				String result = HttpUtil.sendPost(finalUrl, new HashMap<>());
				OtasaasData otasaasData = JSON.parseObject(result, OtasaasData.class);
				BusinessData businessData = getBusinessDatas(channel, otasaasData);
				if (businessData != null)
				{
					businessData.setDate(queryDate);
					businessDataList.add(businessData);
				}
				channelNo++;
			}
			businessDataList.sort(new Comparator<BusinessData>() {
				@Override
				public int compare(BusinessData o1, BusinessData o2)
				{
					return -Float.compare(o1.getCompletedTurnover(), o2.getCompletedTurnover());
				}
			});
			for (BusinessData businessData : businessDataList)
			{
				totalAmount += businessData.getCompletedTurnover();
			}
			for (BusinessData businessData : businessDataList)
			{
				businessData.setTotalAmountOneDay(totalAmount);
			}

			excleData.add(businessDataList);
			totalAmountOneDay = String.valueOf(totalAmount);
			cal.add(Calendar.DATE, 1);
		}

		List<List<String>> finalList = new ArrayList<>();
		finalList.add(getHead());
		for (List<BusinessData> list : excleData)
		{
			for (BusinessData businessData : list)
			{
				List<String> listRow = new ArrayList<>();
				listRow.add(businessData.getDate());
				listRow.add(String.valueOf(businessData.getTotalAmountOneDay()));
				listRow.add(businessData.getChannel());
				listRow.add(String.valueOf(businessData.getChannelType()));
				listRow.add(String.valueOf(businessData.getCompletedTurnover()));
				listRow.add(String.valueOf(businessData.getTotalQty()));
				listRow.add(String.valueOf(businessData.getCompletedQty()));
				listRow.add(String.valueOf(
						new BigDecimal(businessData.getConversionRate() * 100).setScale(2, BigDecimal.ROUND_DOWN))
						+ "%");
				listRow.add(String.valueOf(businessData.getAvgUnitPrice()));

				finalList.add(listRow);
			}

		}
		writeXlsx("/Users/chenwangwang/Desktop/biandata.xlsx", finalList);
	}

	private static BusinessData getBusinessDatas(Channel channel, OtasaasData otasaasData) throws Exception
	{
		if (otasaasData != null && !CollectionUtils.isEmpty(otasaasData.getData()))
		{
			BusinessData businessData = otasaasData.getData().get(0);
			businessData.setChannel(channel.getChannelNameCn());
			businessData.setChannelType(channel.getChannelType());
			if (businessData.getCompletedQty() > 0 && Float.compare(businessData.getCompletedTurnover(), 0f) > 0)
			{
				return businessData;
			}
		}
		else
		{
			throw new Exception("数据有误");
		}
		return null;
	}

	private static List<String> getHead()
	{
		List<String> head = new ArrayList<>();
		head.add("时间");
		head.add("成功交易额");
		head.add("场景");
		head.add("类别");
		head.add("订单金额");
		head.add("订单发起数");
		head.add("订单成功数");
		head.add("订单转化率");
		head.add("平均客单价");
		return head;
	}

	private static List<Channel> onlineChannels = new ArrayList<>();
	static
	{

		onlineChannels.add(new Channel("携程", "jiudian_ctrip", "(酒店)"));
		onlineChannels.add(new Channel("抠电影", "dianying_kou", "(电影)"));
		onlineChannels.add(new Channel("欧飞", "huafei_ofpay", "(话费)"));
		onlineChannels.add(new Channel("大汉三通", "liuliang_dhst", "(流量)"));
		onlineChannels.add(new Channel("邻趣(咖啡)", "coffee_linqu", "(咖啡)"));
		onlineChannels.add(new Channel("饿了么", "waimai_ele", "(外卖)"));
		onlineChannels.add(new Channel("泰笛", "xianhua_tidy", "(鲜花)"));
		onlineChannels.add(new Channel("高阳捷迅", "jiayouka_gaoyang", "(加油卡)"));
		onlineChannels.add(new Channel("驴妈妈", "menpiao_lvmama", "(旅游)"));
		onlineChannels.add(new Channel("邻趣(随意购)", "paotui_linqu", "(？？)"));
		onlineChannels.add(new Channel("易果生鲜", "shengxian_yiguo", "(水果)"));
		onlineChannels.add(new Channel("西十区", "piaowu_xishiqu", "(？？)"));
	}

	static class Channel
	{
		Channel(String channelNameCn, String channelName, String channelType)
		{
			this.channelName = channelName;
			this.channelNameCn = channelNameCn;
			this.channelType = channelType;
		}

		private String channelName;

		private String channelNameCn;

		private String channelType;

		public String getChannelType()
		{
			return channelType;
		}

		public void setChannelType(String channelType)
		{
			this.channelType = channelType;
		}

		public String getChannelName()
		{
			return channelName;
		}

		public void setChannelName(String channelName)
		{
			this.channelName = channelName;
		}

		public String getChannelNameCn()
		{
			return channelNameCn;
		}

		public void setChannelNameCn(String channelNameCn)
		{
			this.channelNameCn = channelNameCn;
		}

		@Override
		public String toString()
		{
			return "Channel{" + "channelName='" + channelName + '\'' + ", channelNameCn='" + channelNameCn + '\'' + '}';
		}
	}

	public static class OtasaasData
	{
		public OtasaasData()
		{

		}

		private String code;

		private String msg;

		private List<BusinessData> data;

		public String getCode()
		{
			return code;
		}

		public void setCode(String code)
		{
			this.code = code;
		}

		public String getMsg()
		{
			return msg;
		}

		public void setMsg(String msg)
		{
			this.msg = msg;
		}

		public List<BusinessData> getData()
		{
			return data;
		}

		public void setData(List<BusinessData> data)
		{
			this.data = data;
		}

		@Override
		public String toString()
		{
			return "OtasaasData{" + "code='" + code + '\'' + ", msg='" + msg + '\'' + ", data=" + data + '}';
		}
	}

	public static class BusinessData
	{
		public float getTotalAmountOneDay()
		{
			return totalAmountOneDay;
		}

		public void setTotalAmountOneDay(float totalAmountOneDay)
		{
			this.totalAmountOneDay = totalAmountOneDay;
		}

		//懒得搞了 随意点写了
		private float totalAmountOneDay;

		private String date;

		private String channel;

		private String channelType;
		//成功交易额
		private float completedTurnover;

		//发起订单数
		private int totalQty;

		//订单转化率
		private float conversionRate;

		//平均客单价
		private float avgUnitPrice;

		// /成功订单数
		private int completedQty;

		public float getCompletedTurnover()
		{
			return completedTurnover;
		}

		public void setCompletedTurnover(float completedTurnover)
		{
			this.completedTurnover = completedTurnover;
		}

		public int getTotalQty()
		{
			return totalQty;
		}

		public void setTotalQty(int totalQty)
		{
			this.totalQty = totalQty;
		}

		public float getConversionRate()
		{
			return conversionRate;
		}

		public void setConversionRate(float conversionRate)
		{
			this.conversionRate = conversionRate;
		}

		public float getAvgUnitPrice()
		{
			return avgUnitPrice;
		}

		public void setAvgUnitPrice(float avgUnitPrice)
		{
			this.avgUnitPrice = avgUnitPrice;
		}

		public int getCompletedQty()
		{
			return completedQty;
		}

		public void setCompletedQty(int completedQty)
		{
			this.completedQty = completedQty;
		}

		public String getChannel()
		{
			return channel;
		}

		public String getChannelType()
		{
			return channelType;
		}

		public void setChannel(String channel)
		{
			this.channel = channel;
		}

		public void setChannelType(String channelType)
		{
			this.channelType = channelType;
		}

		public String getDate()
		{
			return date;
		}

		public void setDate(String date)
		{
			this.date = date;
		}

		@Override
		public String toString()
		{
			return "BusinessData{" + "channel='" + channel + '\'' + ", channelType='" + channelType + '\''
					+ ", completedTurnover=" + completedTurnover + ", totalQty=" + totalQty + ", conversionRate="
					+ conversionRate + ", avgUnitPrice=" + avgUnitPrice + ", completedQty=" + completedQty + '}';
		}

	}

	public static void writeXlsx(String fileName, List<List<String>> data)
	{
		try
		{
			XSSFWorkbook wb = new XSSFWorkbook();

			XSSFSheet sheet = wb.createSheet("1");

			// write body
			for (int i = 0; i < data.size(); i++)
			{
				XSSFRow row = sheet.createRow(i);
				List<String> str = data.get(i);
				for (int j = 0; j < str.size(); j++)
				{
					XSSFCell cell = row.createCell(j);
					cell.setCellValue(str.get(j));
				}
			}

			//			sheet.addMergedRegion(new CellRangeAddress(
			//					i,
			//					(short)1,
			//					2, //last row (0-based)
			//					(short)1  //last column  (0-based)
			//			));
			FileOutputStream outputStream = new FileOutputStream(fileName);
			wb.write(outputStream);
			outputStream.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}
}
