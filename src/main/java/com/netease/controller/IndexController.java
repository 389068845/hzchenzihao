/*
 * FileName: IndexController.java
 * Author:   hzchenzihao
 * Date:     2016年4月29日 下午3:13:27
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.netease.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 〈一句话功能简述〉<br> 
 * 〈功能详细描述〉
 *
 * @author hzchenzihao
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Controller
public class IndexController
{
	@RequestMapping("/")
	@ResponseBody
	public Map<String, String> index()
	{
		Map<String, String> hashMap = new HashMap<String, String>();
		hashMap.put("index", "index");
		return hashMap;
	}
}
