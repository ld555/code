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
import me.chuang6.jz.util.MessageUtils;
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
			map.put("message", MessageUtils.getMsg(0));
		} else {
			map.put("result", -1001);
			map.put("message", MessageUtils.getMsg(-1001));
		}
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/list")
	public Map<String, Object> list(String time, String uuid, String timestamp, String digest) {
		Map<String, Object> map = new HashMap<>();
		int result = userService.vaild(uuid, timestamp, digest);

		if (result != 0) {
			map.put("result", result);
			map.put("message", MessageUtils.getMsg(result));
			return map;
		}
		Date date = TimeUtils.getDate(time);
		map.put("list", infoService.getInfos(date));
		map.put("result", result);
		map.put("message", MessageUtils.getMsg(result));
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/history")
	public Map<String, Object> history(String time, String uuid, String timestamp, String digest) {
		Map<String, Object> map = new HashMap<>();
		int result = userService.vaild(uuid, timestamp, digest);

		if (result != 0) {
			map.put("result", result);
			map.put("message", MessageUtils.getMsg(result));
			return map;
		}
		Date date = TimeUtils.getDate(time);
		map.put("list", infoService.getHistory(date, 30));//获取30天的数据
		map.put("result", result);
		map.put("message", MessageUtils.getMsg(result));
		return map;
	}

}
