package me.chuang6.jz.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import me.chuang6.jz.service.InfoService;
import me.chuang6.jz.service.UserService;
import me.chuang6.jz.util.TimeUtils;

@Controller
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private InfoService infoService;

	@Autowired
	private UserService userService;

	@ResponseBody
	@RequestMapping(value = "/login")
	public Map<String, Object> login(String openid) {
		String uuid = userService.loginUser(openid);
		Map<String, Object> map = new HashMap<>();
		if (uuid != null) {
			map.put("uuid", uuid);
			map.put("result", 0);
			map.put("message", "获取成功");
		}else{
			map.put("result", -1001);
			map.put("message", "登录验证失败,请检查重新登录");
		}
		return map;
	}

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
		map.put("list", infoService.getHistory(date, days));
		map.put("result", 0);
		map.put("message", "获取成功");
		return map;
	}
}
