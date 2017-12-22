package me.chuang6.jz.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.service.InfoService;
import me.chuang6.jz.util.TimeUtils;

@Controller
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private InfoService infoService;

	@ResponseBody
	@RequestMapping(value = "/list")
	public Map<String, Object> list(String time) {
		Date date = TimeUtils.getDate(time);
		Map<String, Object> map = new HashMap<>();
		map.put("list", infoService.getInfos(date));
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/history")
	public Map<String, Object> history(String time, Integer days) {
		Date date = TimeUtils.getDate(time);
		Map<String, Object> map = new HashMap<>();
		// 获取历史数据
		List<Info> historyList = infoService.getHistory(date, days);
		// 分组
		Map<Date, List<Info>> collect = historyList.stream().collect(Collectors.groupingBy(Info::getAddtime));
		// 转成数组格式
		List<Entry<Date, List<Info>>> resultList = new ArrayList<>();

		Iterator<Entry<Date, List<Info>>> iterator = collect.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Date, List<Info>> next = iterator.next();
			resultList.add(next);
		}

		// 按时间排序
		List<Entry<Date, List<Info>>> sortList = resultList.stream().sorted((d1, d2) -> {
			Date date1 = d1.getKey();
			Date date2 = d2.getKey();
			return date1.compareTo(date2);
		}).collect(Collectors.toList());
		
		//Jackson解析时间除了问题，比正常时间少了一天
		map.put("list", sortList);
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}

}
