package me.chuang6.jz.util;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {
	/**
	 * 获取days天前的日期
	 * @param days
	 * @return
	 */
	public static Date getDate(int days){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -days);
		return cal.getTime();
	}
}
