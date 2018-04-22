package me.chuang6.jz.work;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.dao.NoticeMapper;
import me.chuang6.jz.util.PushUtils;
import me.chuang6.jz.util.TextUtils;
import me.chuang6.jz.util.TimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by liuchuang on 2018/4/21.
 */
public class ChongQingWork {

    private static final Logger logger = LoggerFactory.getLogger(ChongQingWork.class);

    private static final String URL = "http://caipiao.163.com/award/cqssc/%s.html";

    @Autowired
    private InfoMapper infoMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 抓取网站数据
     */
    public void scanInfoFromWebSite() {
        logger.info("开始重庆时时彩爬虫任务......");
        getData(TimeUtils.getDate(new Date(), 0));

        /**
         * 如果是凌晨0：10 再去取一下昨天最后一期
         */
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formatTime = sdf.format(new Date());
        String[] split = formatTime.split(":");
        if(Integer.valueOf(split[0])==0&&Integer.valueOf(split[1])<10){
            logger.info("formatTime:{} 获取昨天最后一期数据......",formatTime);
            new Thread(()->getData(TimeUtils.getDate(new Date(), -1))).start();
        }

        logger.info("结束重庆时时彩爬虫任务......");
    }

    private void getData(Date date) {
        String time = TimeUtils.getTime(date, "yyyyMMdd");
        List<Info> list = new ArrayList<>();
        Document doc = null;
        try {
            String url = String.format(URL, time);
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            logger.error("ChongQingWork.getData is error e={}", e);
        }
        if (doc != null) {
            Elements trs = doc.select("tbody").select("tr");
            for (int i = 1; i < trs.size(); i++) {
                Elements tds = trs.get(i).select("td");

                String text = tds.get(0).text();
                String text2 = tds.get(1).text();

                String text3 = tds.get(7).text();
                String text4 = tds.get(8).text();

                String text5 = tds.get(14).text();
                String text6 = tds.get(15).text();

                Info info1 = new Info(Integer.valueOf(text), text2);
                Info info2 = new Info(Integer.valueOf(text3), text4);
                Info info3 = new Info(Integer.valueOf(text5), text6);

                // 倒着添加 因为判断是否存在也是倒着判断最后一个
                list.add(info3);
                list.add(info2);
                list.add(info1);
            }
            Collections.sort(list, new Comparator<Info>() {
                @Override
                public int compare(Info info1, Info info2) {
                    return info1.getPeriods().compareTo(info2.getPeriods());
                }
            });

            insert(list, date);
        }
    }

    private void insert(List<Info> infoList, Date date) {

        for (int i = infoList.size() - 1; i >= 0; i--) {
            Info info = infoList.get(i);
            if (!info.getNumber().contains("-")) {

                if (getCount(info.getPeriods(), date, 0) == 0) {
                    // 插入
                    info.setAddtime(date);
                    info.setSource(0);
                    try {
                        infoMapper.insert(info);
                        logger.info("ChongQingWork.insertInfo is success info:{}", info);
                    } catch (Exception e) {
                        logger.error("ChongQingWork.insertInfo is error e={}", e);
                    }
                    // 获取当前所有数据，看看是否报警

                    InfoExample example = new InfoExample();
                    example.setOrderByClause("addtime desc,periods desc");
                    InfoExample.Criteria createCriteria = example.createCriteria();
                    createCriteria.andSourceEqualTo(0);
                    List<Info> list = null;
                    try {
                        list = infoMapper.selectByExample(example);
                    } catch (Exception e) {
                        logger.error("ChongQingWork.selectInfo is error e={}", e);
                    }
                    String notice = TextUtils.notice(list);
                    if (StringUtils.isNotEmpty(notice)) {
                        // 插入报警数据
                        Notice objNotice = new Notice();
                        objNotice.setNotice(notice);
                        objNotice.setNumber(info.getNumber());
                        objNotice.setPeriods(info.getPeriods());
                        objNotice.setAddtime(date);
                        objNotice.setType(TextUtils.checkType(info.getNumber()));
                        objNotice.setSource(0);
                        try {
                            noticeMapper.insert(objNotice);
                            logger.info("报警数据：{}", objNotice);
                        } catch (Exception e) {
                            logger.error("ChongQingWork.insertNotice is error e={}", e);
                        }

                        // TODO 发送报警短信
                        PushUtils.broadcastAll("重庆时时彩报警", notice, notice, 3);
                    }

                    PushUtils.broadcastAll("重庆时时彩", info.toString(), info.toString(), 4);

                } else {
                    break;// 倒着判断，只要有一个存在，那么前面的肯定也都存在 所以直接跳出循环
                }
            }
        }

    }

    private long getCount(Integer periods, Date date, Integer source) {
        InfoExample example = new InfoExample();
        InfoExample.Criteria createCriteria = example.createCriteria();
        createCriteria.andAddtimeEqualTo(date);
        createCriteria.andPeriodsEqualTo(periods);
        createCriteria.andSourceEqualTo(source);
        long result = -1;
        try {
            result = infoMapper.countByExample(example);
        } catch (Exception e) {
            logger.error("ChongQingWork.getCount is error e={}", e);
        }
        return result;
    }
}
