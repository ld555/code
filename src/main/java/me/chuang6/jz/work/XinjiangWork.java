package me.chuang6.jz.work;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.service.InfoService;
import me.chuang6.jz.service.NoticeService;
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

/**
 * Created by liuchuang on 2018/4/21.
 */
public class XinjiangWork {

    private static final Logger logger = LoggerFactory.getLogger(XinjiangWork.class);

    @Autowired
    private InfoService infoService;

    @Autowired
    private NoticeService noticeService;

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
            if (infoService.getCount(periods, TimeUtils.getDate2(time.substring(0, 8)), 1) == 0) {
                Info info = new Info();
                info.setSource(1);
                info.setAddtime(TimeUtils.getDate2(time.substring(0, 8)));
                info.setNumber(number);
                info.setPeriods(periods);
                infoService.insert(info);
                PushUtils.broadcastAll("新疆时时彩", info.toString(), info.toString(), 2);

                // 获取当前所有数据，看看是否报警
                String notice = TextUtils.notice(infoService.getInfosLimit(1));
                if (StringUtils.isNotEmpty(notice)) {
                    // 插入报警数据
                    Notice objNotice = new Notice();
                    objNotice.setNotice(notice);
                    objNotice.setNumber(info.getNumber());
                    objNotice.setPeriods(info.getPeriods());
                    objNotice.setAddtime(TimeUtils.getDate2(time.substring(0, 8)));
                    objNotice.setType(TextUtils.checkType(info.getNumber()));
                    objNotice.setSource(1);
                    noticeService.insert(objNotice);
                    PushUtils.broadcastAll("新疆时时彩报警", notice, notice, 1);
                }
            }
        }
    }
}
