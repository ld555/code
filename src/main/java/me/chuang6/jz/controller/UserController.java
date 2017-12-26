package me.chuang6.jz.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import me.chuang6.jz.bean.User;
import me.chuang6.jz.service.UserService;
import me.chuang6.jz.util.VCodeUtils;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpSession session, String username, String password, String vcode) {
		String code = (String) session.getAttribute("vcode");
		if (!vcode.equalsIgnoreCase(code)) {
			session.setAttribute("login_msg", "验证码错误");
			return "redirect:/index.jsp";
		}
		if ("litao".equals(username) && "litao888.".equals(password)) {
			session.setAttribute("username", username);
			return "redirect:/user/list";
		}
		session.setAttribute("login_msg", "用户名或密码错误");
		return "redirect:/index.jsp";

	}

	@RequestMapping(value = "/list")
	public String list(HttpSession session, HttpServletRequest request,
			@RequestParam(value = "pn", defaultValue = "1") Integer pn) {
		String username = (String) session.getAttribute("username");
		if (null != username) {
			PageHelper.startPage(pn, 10);
			List<User> list = userService.getList();
			PageInfo<User> pageInfo = new PageInfo<User>(list, 10);
			request.setAttribute("pageInfo", pageInfo);
			return "/list";
		}
		return "redirect:/index.jsp";
	}

	@RequestMapping(value = "/add")
	public String add(User user, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "pn", defaultValue = "1") Integer pn) throws Exception {
		String username = (String) session.getAttribute("username");
		if (null != username) {
			userService.add(user);
			return "redirect:/user/list";
		}
		return "redirect:/index.jsp";
	}

	@RequestMapping(value = "/vcode")
	public void vcode(HttpSession session, HttpServletResponse response) throws IOException {
		VCodeUtils utils = new VCodeUtils();
		BufferedImage image = utils.getImage();
		VCodeUtils.output(image, response.getOutputStream());
		session.setAttribute("vcode", utils.getText());
	}
}
