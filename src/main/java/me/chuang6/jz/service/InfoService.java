package me.chuang6.jz.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.InfoExample.Criteria;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.util.TextUtils;
import me.chuang6.jz.util.TimeUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Service
public class InfoService {

	@Autowired
	private InfoMapper infoMapper;
	
	@Autowired
	private JedisPool jedisPool;

	/**
	 * 获取某日的数据
	 * 
	 * @param date
	 * @return
	 */
	public List<Info> getInfos(Date date) {
		InfoExample example = new InfoExample();
		example.setOrderByClause("periods asc");
		Criteria createCriteria = example.createCriteria();
		createCriteria.andAddtimeEqualTo(date);
		return infoMapper.selectByExample(example);
	}

	/**
	 * 获取某日前days天的数据
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public List<Info> getInfos(Date date, Integer days) {
		InfoExample example = new InfoExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andAddtimeBetween(TimeUtils.getDate(date, -days), TimeUtils.getDate(date, -1));
		return infoMapper.selectByExample(example);
	}

	/**
	 * 获取历史数据
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public List<String[]> getHistory(Date date, Integer days) {
		List<String[]> result = new ArrayList<>();
		String key = TimeUtils.getTime(date);
		Jedis jedis = jedisPool.getResource();
		jedis.select(1);
		String value = jedis.get(key);
		if (value == null) {
			// 从数据库中获取历史数据
			List<Info> historyData = getInfos(date, days);
			// 格式化数据
			List<Entry<Date, List<Info>>> historyParseData = parseData(historyData);
			// 计算数量
			List<String[]> historyList = calculate(historyParseData);
			result.addAll(historyList);
			String json = JSON.toJSONString(historyList);
			jedis.set(key, json);
			jedis.expire(key, 3600 * 24 * 7);
		} else {

			List<String[]> parseObject = JSON.parseObject(value, new TypeReference<List<String[]>>() {
			});

			result.addAll(parseObject);
		}
		result.addAll(calculate(parseData(getInfos(date))));
		jedis.close();
		return result;
	}

	/**
	 * 处理数据格式
	 * 
	 * @param dataList
	 * @return
	 */
	private List<Entry<Date, List<Info>>> parseData(List<Info> dataList) {
		// 将数据按时间分组
		Map<Date, List<Info>> groupData = dataList.stream().collect(Collectors.groupingBy(Info::getAddtime));

		// 转成数组格式
		List<Entry<Date, List<Info>>> resultList = new ArrayList<>();
		Iterator<Entry<Date, List<Info>>> iterator = groupData.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Date, List<Info>> next = iterator.next();
			resultList.add(next);
		}

		// 按时间排序
		List<Entry<Date, List<Info>>> sortList = resultList.stream().sorted((d1, d2) -> {
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
	private List<String[]> calculate(List<Entry<Date, List<Info>>> list) {

		List<String[]> result = new ArrayList<>();

		for (int i = 0; i < list.size(); i++) {
			Map.Entry<Date, List<Info>> map = list.get(i);
			List<Info> infoList = map.getValue();
			String[] counts = new String[] { "0", "0", "0", "0", "0", "0", "0" };

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
