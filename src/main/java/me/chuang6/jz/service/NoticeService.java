package me.chuang6.jz.service;

import me.chuang6.jz.bean.Notice;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface NoticeService {

    List<Notice> getNoticeList(Integer source);

    int insert(Notice notice);

}
