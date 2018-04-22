package me.chuang6.jz.util;

import me.chuang6.jz.bean.Info;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class TextUtils {

    /**
     * 报警逻辑 1、60有7把不出 2、120有15把不出 3、30有42把不出 4，30和豹子同时有19把不出
     */
    public static String notice(List<Info> list) {
        if (list != null && list.size() < 7) return null;
        int s120 = 0;
        int s60 = 0;
        int s30 = 0;
        int bz30 = 0;// 豹子30
        boolean b_120 = false;
        boolean b_60 = false;
        boolean b_30 = false;
        boolean b_bz30 = false;
        for (int i = 0; i < list.size(); i++) {
            String number = list.get(i).getNumber();
            int result = checkNum(number);
            if (!b_bz30) {
                if (result != 2 && result != 3) {
                    bz30++;
                } else {
                    b_bz30 = true;
                }
            }
            if (!b_120) {
                if (result != 0) {
                    s120++;
                } else {
                    b_120 = true;
                }
            }
            if (!b_30) {
                if (result != 2) {
                    s30++;
                } else {
                    b_30 = true;
                }
            }
            if (!b_60) {
                if (result != 1) {
                    s60++;
                } else {
                    b_60 = true;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        if (s120 >= 10) {
            sb.append(String.format("<120>连续%d期没有出现 ", s120));
        }
        if (s60 >= 7) {
            sb.append(String.format("<60>连续%d期没有出现 ", s60));
        }
        if (s30 >= 25) {
            sb.append(String.format("<30>连续%d期没有出现 ", s30));
        }
        if (bz30 >= 19) {
            sb.append(String.format("<30>和<豹子>同时连续%d期没有出现 ", bz30));
        }
        return sb.toString().trim();
    }

    public static String checkType(String number) {
        int input = checkNum(number);
        String type = null;
        switch (input) {
            case 0:
                type = "120";
                break;
            case 1:
                type = "60";
                break;
            case 2:
                type = "30";
                break;
            case 3:
                type = "豹子";
                break;
            case 4:
                type = "四连";
                break;
            case 5:
                type = "五连";
                break;
        }
        return type;
    }

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

            if (value == 3) {// 如果value中有等于3的 那就说明是豹子
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
                result = 3;
            }
        } else if (map.size() == 1) {
            // 五连
            result = 5;
        }
        return result;
    }
}
