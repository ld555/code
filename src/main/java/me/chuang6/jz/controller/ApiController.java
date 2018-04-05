package me.chuang6.jz.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.service.InfoService;
import me.chuang6.jz.service.NoticeService;
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

	@Autowired
	private NoticeService noticeService;

	@RequestMapping(value = "logs")
	public String log(@RequestParam(value = "pn", defaultValue = "1") Integer pn, HttpServletRequest request) {
		try {
			PageHelper.startPage(pn, 10);
			List<Notice> list = noticeService.getNoticeList();
			PageInfo<Notice> pageInfo = new PageInfo<Notice>(list, 10);
			request.setAttribute("pageInfo", pageInfo);
			return "logs";
		} catch (Exception e) {
			request.setAttribute("msg", e.getMessage());
			return "error";
		}
	}

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
	public Map<String, Object> list(String time, String uuid, String timestamp, String digest, String source) {
		Map<String, Object> map = new HashMap<>();
		int result = userService.vaild(uuid, timestamp, digest);

		if (result != 0) {
			map.put("result", result);
			map.put("message", MessageUtils.getMsg(result));
			return map;
		}
		Date date = TimeUtils.getDate(time);
		if (StringUtils.isBlank(source)) {
			map.put("list", infoService.getInfos(date));
		} else {
			if ("chongqing".equals(source)) {
				map.put("list", infoService.getInfos(date));
			} else if ("xinjiang".equals(source)) {

			}
		}
		map.put("result", result);
		map.put("message", MessageUtils.getMsg(result));
		return map;
	}

	@ResponseBody
	@RequestMapping(value = "/history")
	public Map<String, Object> history(String time, String uuid, String timestamp, String digest, String source) {
		Map<String, Object> map = new HashMap<>();
		int result = userService.vaild(uuid, timestamp, digest);

		if (result != 0) {
			map.put("result", result);
			map.put("message", MessageUtils.getMsg(result));
			return map;
		}
		Date date = TimeUtils.getDate(time);
		if (StringUtils.isBlank(source)) {
			map.put("list", infoService.getHistory(date, 30));// 获取30天的数据
		} else {
			if ("chongqing".equals(source)) {
				map.put("list", infoService.getHistory(date, 30));// 获取30天的数据
			} else if ("xinjiang".equals(source)) {

			}
		}

		map.put("result", result);
		map.put("message", MessageUtils.getMsg(result));
		return map;
	}

}
