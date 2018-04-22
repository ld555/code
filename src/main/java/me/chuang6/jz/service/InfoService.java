package me.chuang6.jz.service;

import me.chuang6.jz.bean.Info;

import java.util.Date;
import java.util.List;


public interface InfoService {
    List<Info> getInfos(Date date, Integer source);

    List<Info> getInfos(Date date, Integer days, Integer source);

    List<String[]> getHistory(Date date, Integer days, Integer source);

    long getCount(Integer periods, Date date, Integer source);

    int insert(Info info);

    List<Info> getInfosLimit(int source);
}
