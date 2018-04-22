package me.chuang6.jz.service.impl;

import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.bean.NoticeExample;
import me.chuang6.jz.dao.NoticeMapper;
import me.chuang6.jz.service.NoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuchuang on 2018/4/21.
 */
@Service
public class NoticeServiceImpl implements NoticeService{

    private static final Logger logger = LoggerFactory.getLogger(NoticeServiceImpl.class);

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public List<Notice> getNoticeList(Integer source) {
        NoticeExample example = new NoticeExample();
        example.setOrderByClause("id desc");
        example.createCriteria().andSourceEqualTo(source);
        return noticeMapper.selectByExample(example);
    }

    @Override
    public int insert(Notice notice) {
        logger.info("NoticeServiceImpl.insert notice={}",notice);
        return noticeMapper.insert(notice);
    }
}
