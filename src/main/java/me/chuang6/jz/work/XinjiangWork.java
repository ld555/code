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

import java.util.Date;
import java.util.List;

/**
 * Created by liuchuang on 2018/4/21.
 */
public class XinjiangWork {

    private static final Logger logger = LoggerFactory.getLogger(XinjiangWork.class);

    @Autowired
    private InfoMapper infoMapper;

    @Autowired
    private NoticeMapper noticeMapper;

    /**
     * 抓取网站数据
     */
    public void scanInfoFromWebSite() {
        logger.info("开始新疆时时彩爬虫任务......");
        getData();
        logger.info("结束新疆时时彩爬虫任务......");
    }

    private void getData() {
        Document doc = null;
        try {
            String url = String.format("http://www.xjflcp.com/game/sscIndex");
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            logger.error("XinjiangWork.getData is error e={}", e);
        }
        if (doc != null) {
            Elements select = doc.select("div.con_left");
            Elements select2 = select.select("span");
            Elements select3 = select.select("i");
            String time = select2.get(0).text().trim();// 2018040996
            String number = select3.text().trim();// 5 5 8 0 0
            int periods = Integer.valueOf(time.substring(8));
            if (getCount(periods, TimeUtils.getDate(time.substring(0, 8)), 1) == 0) {
                Info info = new Info();
                info.setSource(1);
                info.setAddtime(TimeUtils.getDate(time.substring(0, 8)));
                info.setNumber(number);
                info.setPeriods(periods);

                try {
                    infoMapper.insert(info);
                    logger.info("XinjiangWork.insertInfo is success info:{}", info);
                } catch (Exception e) {
                    logger.error("XinjiangWork.insert is error e={}", e);
                }
                // 获取当前所有数据，看看是否报警

                InfoExample example = new InfoExample();
                example.setOrderByClause("addtime desc,periods desc");
                InfoExample.Criteria createCriteria = example.createCriteria();
                createCriteria.andSourceEqualTo(1);
                List<Info> list2 = null;
                try {
                    list2 = infoMapper.selectByExampleLimit(example);
                } catch (Exception e) {
                    logger.error("XinjiangWork.selectInfo is error e={}", e);
                }
                String notice = TextUtils.notice(list2);
                if (StringUtils.isNotEmpty(notice)) {
                    // 插入报警数据
                    Notice objNotice = new Notice();
                    objNotice.setNotice(notice);
                    objNotice.setNumber(info.getNumber());
                    objNotice.setPeriods(info.getPeriods());
                    objNotice.setAddtime(TimeUtils.getDate(time));
                    objNotice.setType(TextUtils.checkType(info.getNumber()));
                    objNotice.setSource(1);
                    try {
                        noticeMapper.insert(objNotice);
                        logger.info("报警数据：{}", objNotice);
                    } catch (Exception e) {
                        logger.error("XinjiangWork.insertNotice is error e={}", e);
                    }

                    // TODO 发送报警短信
                    PushUtils.broadcastAll("新疆时时彩报警", notice, notice, 1);
                }

                PushUtils.broadcastAll("新疆时时彩", info.toString(), info.toString(), 2);
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
            logger.error("XinjiangWork.selectCount is error e={}", e);
        }
        return result;
    }
}
