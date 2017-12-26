package me.chuang6.jz.util;

public class MessageUtils {

	public static String getMsg(int result) {
		String msg = null;
		switch (result) {
		case -1001:
			msg = "登录失败,请检查重新登录";
			break;
		case -1002:
			msg = "参数错误";
			break;
		case -1003:
			msg = "摘要错误";
			break;
		case -1004:
			msg = "登录过期,请重新登录";
			break;
		case -1005:
			msg = "被其他人顶替登录,请重新登录";
			break;
		default:
			msg = "处理成功";
			break;
		}
		return msg;
	}
}
