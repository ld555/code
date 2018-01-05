package me.chuang6.jz.util;

import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Sender;

public class PushUtils {
	private static final String MY_PACKAGE_NAME = "com.liuchuang.caipiao";
	private static final String APP_SECRET_KEY = "dc2D+Bf4F4R8e/mcOMm0Ww==";

	public static void broadcastAll(String title, String description, String messagePayload, int id) {
		Constants.useOfficial();
		Sender sender = new Sender(APP_SECRET_KEY);
		Message message = new Message.Builder() //
				.payload(messagePayload)// 要发的内容
				.title(title) // 通知栏标题
				.description(description) // 通知栏描述
				// .passThrough(1)//是否透传
				.notifyType(-1) // 设置通知类型
				.restrictedPackageName(MY_PACKAGE_NAME) // 设置app的包名packageName
				.notifyId(id) // 通知id相同的话会覆盖
				.build();
		try {
			sender.broadcastAll(message, 3);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
