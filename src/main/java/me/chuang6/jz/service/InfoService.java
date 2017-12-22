package me.chuang6.jz.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.InfoExample.Criteria;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.util.TimeUtils;

@Service
public class InfoService {

	@Autowired
	private InfoMapper infoMapper;

	public List<Info> getInfos(Date date) {
		InfoExample example = new InfoExample();
		example.setOrderByClause("periods asc");
		Criteria createCriteria = example.createCriteria();
		createCriteria.andAddtimeEqualTo(date);
		return infoMapper.selectByExample(example);
	}
	/**
	 * 获取历史数据
	 * @param date
	 * @param days
	 * @return
	 */
	public List<Info> getHistory(Date date, Integer days) {
		InfoExample example = new InfoExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andAddtimeBetween(TimeUtils.getDate(days), date);
		return infoMapper.selectByExample(example);
	}
}
