package me.chuang6.jz.util;

public class MessageUtils {

	public static String getMsg(int result) {
		String msg = null;
		switch (result) {
		case -1001:
			msg = "验证码不正确";
			break;
		case -1002:
			msg = "参数不正确";
			break;
		case -1003:
			msg = "摘要不正确";
			break;
		case -1004:
			msg = "请重新登录（原因：UUID过期）";
			break;
		case -1005:
			msg = "请重新登录（原因：被他人顶替登录）";
			break;
		case -1006:
			msg = "接口请求超时";
			break;
		default:
			msg = "处理成功";
			break;
		}
		return msg;
	}
}
