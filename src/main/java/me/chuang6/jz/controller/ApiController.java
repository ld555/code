package me.chuang6.jz.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
}
