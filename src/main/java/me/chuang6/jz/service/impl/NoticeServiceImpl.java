package me.chuang6.jz.service.impl;

import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.bean.NoticeExample;
import me.chuang6.jz.dao.NoticeMapper;
import me.chuang6.jz.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuchuang on 2018/4/21.
 */
@Service
public class NoticeServiceImpl implements NoticeService{
    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public List<Notice> getNoticeList(Integer source) {
        NoticeExample example = new NoticeExample();
        example.setOrderByClause("id desc");
        example.createCriteria().andSourceEqualTo(source);
        return noticeMapper.selectByExample(example);
    }
}
