package me.chuang6.jz.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class TextUtils {
	/**
	 * 检查号码是什么类型
	 * 
	 * @param number
	 * @return
	 */
	public static int checkNum(String number) {
		int result = 0;// 全单
		Map<String, Integer> map = new HashMap<>();

		String[] split = number.split(" ");
		// 初始化map
		for (String s : split) {
			map.put(s, 0);
		}
		// 统计数字
		for (String s : split) {
			int count = map.get(s).intValue();
			map.put(s, ++count);
		}
		// 判断类型
		if (map.size() == 5) {// 123456
			// 全单
			result = 0;
		} else if (map.size() == 4) {// 12341
			// 一对
			result = 1;
		} else if (map.size() == 3) { // 11123 11233
			// 两对 或 豹子
			Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();

			int value = 0;

			while (iterator.hasNext()) {
				Integer max = iterator.next().getValue();
				if (max > value) {
					value = max;
				}
			}

			if (value == 3) {//如果value中有等于3的 那就说明是豹子
				result = 3;
			} else {
				result = 2;
			}
		} else if (map.size() == 2) {
			// 豹子 或 四连
			Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();

			int value = iterator.next().getValue();// 拿出来第一个key的value

			if (value == 1 || value == 4) {
				result = 4;
			} else {
				value = 3;
			}
		} else if (map.size() == 1) {
			// 五连
			result = 5;
		}

		// if (Pattern.compile("(\\w)\\1{4,}").matcher(number).find()) {
		// //五连
		// result = 5;
		// } else if (Pattern.compile("(\\w)\\1{3,}").matcher(number).find()) {
		// //四连
		// result = 4;
		// } else if (Pattern.compile("(\\w)\\1{2,}").matcher(number).find()) {
		// //三连
		// result = 3;
		// } else if (Pattern.compile("(\\w)\\1{1,}").matcher(number).find()) {
		// Matcher matcher = Pattern.compile("(\\w)\\1{1,}").matcher(number);
		// int count = 0;
		// while (matcher.find()) {
		// count++;
		// }
		// if (count == 1) {
		// //一对
		// result = 1;
		// } else {
		// //两对
		// result = 2;
		// }
		//
		// }
//		System.out.println(number + "======" + result);
		return result;
	}
}
