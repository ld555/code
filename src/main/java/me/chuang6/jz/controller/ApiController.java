package me.chuang6.jz.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.chuang6.jz.bean.HotNumber;
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
	@RequestMapping(value = "/hot")
	public Map<String, Object> hot(String time) throws ParseException {
		Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(time);
		Map<String, Object> map = new HashMap<>();
		Map<String, Integer> resultMap = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			resultMap.put(String.valueOf(i), 0);
		}
		List<Info> infos = infoService.getInfos(parse);
		// 统计
		for (int i = 0; i < infos.size(); i++) {
			Info info = infos.get(i);
			String[] split = info.getNumber().split(" ");
			for (int j = 0; j < split.length; j++) {
				String key = split[j];
				int value = resultMap.get(key);
				resultMap.put(key, value + 1);
			}
		}

		Iterator<Entry<String, Integer>> iterator = resultMap.entrySet().iterator();
		List<HotNumber> hotNumbers = new ArrayList<>();
		while (iterator.hasNext()) {
			hotNumbers.add(new HotNumber(iterator.next().getKey(), iterator.next().getValue()));
		}

		Collections.sort(hotNumbers, new Comparator<HotNumber>() {
			@Override
			public int compare(HotNumber o1, HotNumber o2) {
				return -o1.getCount().compareTo(o2.getCount());
			}
		});

		map.put("list", hotNumbers);
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}
}
