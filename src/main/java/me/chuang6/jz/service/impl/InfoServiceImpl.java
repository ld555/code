package me.chuang6.jz.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.cache.JedisClient;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.service.InfoService;
import me.chuang6.jz.util.TextUtils;
import me.chuang6.jz.util.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liuchuang on 2018/4/21.
 */
@Service
public class InfoServiceImpl implements InfoService{
    @Value("${REDIS_CAIPIAO_HISTORY_INFO_KEY}")
    private String REDIS_CAIPIAO_HISTORY_INFO;

    @Autowired
    private InfoMapper infoMapper;

    @Autowired
    private JedisClient jedisClient;

    /**
     * 获取某日的数据
     *
     * @param date
     * @return
     */
    @Override
    public List<Info> getInfos(Date date, Integer source) {
        InfoExample example = new InfoExample();
        example.setOrderByClause("periods asc");
        InfoExample.Criteria createCriteria = example.createCriteria();
        createCriteria.andAddtimeEqualTo(date);
        createCriteria.andSourceEqualTo(source);
        return infoMapper.selectByExample(example);
    }

    /**
     * 获取某日前days天的数据
     *
     * @param date
     * @param days
     * @return
     */
    @Override
    public List<Info> getInfos(Date date, Integer days, Integer source) {
        InfoExample example = new InfoExample();
        InfoExample.Criteria createCriteria = example.createCriteria();
        createCriteria.andAddtimeBetween(TimeUtils.getDate(date, -days), TimeUtils.getDate(date, -1));
        createCriteria.andSourceEqualTo(source);
        return infoMapper.selectByExample(example);
    }

    /**
     * 获取历史数据
     *
     * @param date
     * @param days
     * @return
     */
    @Override
    public List<String[]> getHistory(Date date, Integer days, Integer source) {
        List<String[]> result = new ArrayList<>();
        String key = REDIS_CAIPIAO_HISTORY_INFO + ":" + TimeUtils.getTime(date) + ":" + source;
        String value = jedisClient.get(key);
        if (value == null) {
            // 从数据库中获取历史数据
            List<Info> historyData = getInfos(date, days, source);
            // 格式化数据
            List<Map.Entry<Date, List<Info>>> historyParseData = parseData(historyData);
            // 计算数量
            List<String[]> historyList = calculate(historyParseData);
            result.addAll(historyList);
            String json = JSON.toJSONString(historyList);
            jedisClient.set(key, json);
            jedisClient.expire(key, 3600 * 24 * 7);
        } else {

            List<String[]> parseObject = JSON.parseObject(value, new TypeReference<List<String[]>>() {
            });

            result.addAll(parseObject);
        }
        result.addAll(calculate(parseData(getInfos(date, source))));
        return result;
    }

    /**
     * 处理数据格式
     *
     * @param dataList
     * @return
     */
    private List<Map.Entry<Date, List<Info>>> parseData(List<Info> dataList) {
        // 将数据按时间分组
        Map<Date, List<Info>> groupData = dataList.stream().collect(Collectors.groupingBy(Info::getAddtime));

        // 转成数组格式
        List<Map.Entry<Date, List<Info>>> resultList = new ArrayList<>();
        Iterator<Map.Entry<Date, List<Info>>> iterator = groupData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Date, List<Info>> next = iterator.next();
            resultList.add(next);
        }

        // 按时间排序
        List<Map.Entry<Date, List<Info>>> sortList = resultList.stream().sorted((d1, d2) -> {
            Date date1 = d1.getKey();
            Date date2 = d2.getKey();
            return date1.compareTo(date2);
        }).collect(Collectors.toList());

        return sortList;
    }

    /**
     * 计算各个类别数量
     *
     * @param list
     * @return
     */
    private List<String[]> calculate(List<Map.Entry<Date, List<Info>>> list) {

        List<String[]> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map.Entry<Date, List<Info>> map = list.get(i);
            List<Info> infoList = map.getValue();
            String[] counts = new String[]{"0", "0", "0", "0", "0", "0", "0"};

            counts[6] = TimeUtils.getTime(map.getKey());

            for (int j = 0; j < infoList.size(); j++) {
                Info info = infoList.get(j);
                int checkNum = TextUtils.checkNum(info.getNumber());
                int total = Integer.valueOf(counts[checkNum]);
                counts[checkNum] = String.valueOf(++total);
            }

            result.add(counts);
        }
        return result;
    }
}
