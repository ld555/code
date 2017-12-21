package me.chuang6.jz.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.InfoExample.Criteria;
import me.chuang6.jz.dao.InfoMapper;

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
}
