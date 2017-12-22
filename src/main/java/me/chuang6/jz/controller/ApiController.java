package me.chuang6.jz.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

@Controller
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private InfoService infoService;

	@ResponseBody
	@RequestMapping(value = "/list")
	public Map<String, Object> list(String time) throws ParseException {
		Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
		Map<String, Object> map = new HashMap<>();
		map.put("list", infoService.getInfos(parse));
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/history")
	public Map<String, Object> history(String time, Integer days) throws ParseException {
		Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
		Map<String, Object> map = new HashMap<>();
		// 获取历史数据
		List<Info> history = infoService.getHistory(parse, days);
		// 按时间分组
		Map<Date, List<Info>> collect = history.stream().collect(Collectors.groupingBy(Info::getAddtime));
		// 添加当天的数据
		collect.put(parse, infoService.getInfos(parse));
		// 转成数组格式
		List<Entry<Date, List<Info>>> resultList = new ArrayList<>();
		Iterator<Entry<Date, List<Info>>> iterator = collect.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Date, List<Info>> next = iterator.next();
			resultList.add(next);
		}
		map.put("list", resultList);
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}

	// @ResponseBody
	// @RequestMapping(value = "/hot")
	// public Map<String, Object> hot(String time) throws ParseException {
	// Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
	// Map<String, Object> map = new HashMap<>();
	// Map<String, Integer> resultMap = new HashMap<>();
	// for (int i = 0; i < 10; i++) {
	// resultMap.put(String.valueOf(i), 0);
	// }
	// List<Info> infos = infoService.getInfos(parse);
	// // 统计
	// for (int i = 0; i < infos.size(); i++) {
	// Info info = infos.get(i);
	// System.out.println(info);
	// String[] split = info.getNumber().split(" ");
	// for (int j = 0; j < split.length; j++) {
	// String key = split[j];
	// int value = resultMap.get(key);
	// resultMap.put(key, value + 1);
	// }
	// }
	//
	// Iterator<Entry<String, Integer>> iterator =
	// resultMap.entrySet().iterator();
	// List<HotNumber> hotNumbers = new ArrayList<>();
	// while (iterator.hasNext()) {
	// Entry<String, Integer> next = iterator.next();
	// String key = next.getKey();
	// int value = next.getValue();
	// hotNumbers.add(new HotNumber(key, value));
	// }
	//
	// Collections.sort(hotNumbers, new Comparator<HotNumber>() {
	// @Override
	// public int compare(HotNumber o1, HotNumber o2) {
	// return -o1.getCount().compareTo(o2.getCount());
	// }
	// });
	//
	// map.put("list", hotNumbers);
	// map.put("result", 0);
	// map.put("message", "获取成功");
	// return map;
	// }
}
