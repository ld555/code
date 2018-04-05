package me.chuang6.jz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.bean.NoticeExample;
import me.chuang6.jz.dao.NoticeMapper;

@Service
public class NoticeService {

	@Autowired
	private NoticeMapper noticeMapper;
	
	
	public List<Notice> getNoticeList(){
		NoticeExample example = new NoticeExample();
		example.setOrderByClause("id desc");
		return noticeMapper.selectByExample(example);
	}
	
}
