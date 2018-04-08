package me.chuang6.jz.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.InfoExample.Criteria;
import me.chuang6.jz.bean.Notice;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.dao.NoticeMapper;
import me.chuang6.jz.util.PushUtils;
import me.chuang6.jz.util.TextUtils;
import me.chuang6.jz.util.TimeUtils;

public class MyListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(MyListener.class);
    private static final int PERIOD_TIME = 1000 * 30;
    private static final String URL = "http://caipiao.163.com/award/cqssc/%s.html";

    private InfoMapper infoMapper;
    private NoticeMapper noticeMapper;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("监听器启动");
        ServletContext context = sce.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        infoMapper = ctx.getBean(InfoMapper.class);
        noticeMapper = ctx.getBean(NoticeMapper.class);
        Timer timer = new Timer();
        Timer timer2 = new Timer();
        Timer timer3 = new Timer();
        timer3.schedule(new TimerTask() {
            @Override
            public void run() {
                getData2();
            }
        }, 0, PERIOD_TIME);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getData(TimeUtils.getDate(new Date(), 0));
            }
        }, 0, PERIOD_TIME);
        timer2.schedule(new TimerTask() {
            @Override
            public void run() {
                getData(TimeUtils.getDate(new Date(), -1));
            }
        }, 0, PERIOD_TIME);
    }

    private void getData2() {
        Document doc = null;
        try {
            String url = String.format("http://www.xjflcp.com/game/sscIndex");
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            e.printStackTrace();

        }
        if (doc != null) {
            Elements elements = doc.select("tbody");
            List<Info> list = new ArrayList<>();
            for (int i = 0; i < 3; i++) {

                Elements trs = elements.get(i).select("tr");

                for (int j = 1; j < trs.size(); j++) {
                    String td1 = trs.get(j).select("td").get(0).text();//期号
                    String td2 = trs.get(j).select("td").get(1).text();//号码
                    if (!"--".equals(td1)) {
                        String td1_result = td1.substring(8, 10);
                        String td2_result = td2.replace(",", " ");
                        Info info = new Info(Integer.valueOf(td1_result), td2_result);
                        list.add(info);
                    }
                }
            }
            Collections.sort(list, new Comparator<Info>() {
                @Override
                public int compare(Info info1, Info info2) {
                    return info1.getPeriods().compareTo(info2.getPeriods());
                }
            });
            insert(list);
        }
    }

    private void insert(List<Info> infoList) {

        for (int i = infoList.size() - 1; i >= 0; i--) {
            Info info = infoList.get(i);
            if (getCount(info.getPeriods(), new Date(), 1) == 0) {
                info.setSource(1);
                info.setAddtime(new Date());
                try {
                    infoMapper.insert(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 获取当前所有数据，看看是否报警
                try {
                    InfoExample example = new InfoExample();
                    example.setOrderByClause("periods asc");
                    Criteria createCriteria = example.createCriteria();
                    createCriteria.andAddtimeEqualTo(new Date());
                    createCriteria.andSourceEqualTo(1);
                    List<Info> list = null;
                    try {
                        list = infoMapper.selectByExample(example);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    String notice = TextUtils.notice(list);

                    logger.info("报警数据：" + notice);
                    // 插入报警数据
                    Notice objNotice = new Notice();
                    objNotice.setNotice(StringUtils.isEmpty(notice) ? "无" : notice);
                    objNotice.setNumber(info.getNumber());
                    objNotice.setPeriods(info.getPeriods());
                    objNotice.setAddtime(new Date());
                    objNotice.setType(TextUtils.checkType(info.getNumber()));
                    objNotice.setSource(1);
                    try {
                        noticeMapper.insert(objNotice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (StringUtils.isNotEmpty(notice)) {
                        //TODO 发送报警短信
                        PushUtils.broadcastAll("报警", notice, notice, 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                PushUtils.broadcastAll("时时彩助手", info.toString(), info.toString(), 3);

            } else {
                break;// 倒着判断，只要有一个存在，那么前面的肯定也都存在 所以直接跳出循环
            }
        }

    }

    private void getData(Date date) {
        String time = TimeUtils.getTime(date, "yyyyMMdd");
        List<Info> list = new ArrayList<>();
        Document doc = null;
        try {
            String url = String.format(URL, time);
            doc = Jsoup.connect(url).get();
        } catch (Exception e) {
            e.printStackTrace();

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // 获取当前所有数据，看看是否报警
                    try {
                        InfoExample example = new InfoExample();
                        example.setOrderByClause("periods asc");
                        Criteria createCriteria = example.createCriteria();
                        createCriteria.andAddtimeEqualTo(date);
                        createCriteria.andSourceEqualTo(0);
                        List<Info> list = null;
                        try {
                            list = infoMapper.selectByExample(example);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String notice = TextUtils.notice(list);

                        logger.info("报警数据：" + notice);
                        // 插入报警数据
                        Notice objNotice = new Notice();
                        objNotice.setNotice(StringUtils.isEmpty(notice) ? "无" : notice);
                        objNotice.setNumber(info.getNumber());
                        objNotice.setPeriods(info.getPeriods());
                        objNotice.setAddtime(new Date());
                        objNotice.setType(TextUtils.checkType(info.getNumber()));
                        objNotice.setSource(0);
                        try {
                            noticeMapper.insert(objNotice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (StringUtils.isNotEmpty(notice)) {
                            //TODO 发送报警短信
                            PushUtils.broadcastAll("报警", notice, notice, 1);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    PushUtils.broadcastAll("时时彩助手", info.toString(), info.toString(), 0);

                } else {
                    break;// 倒着判断，只要有一个存在，那么前面的肯定也都存在 所以直接跳出循环
                }
            }
        }

    }

    private long getCount(Integer periods, Date date, Integer source) {
        InfoExample example = new InfoExample();
        Criteria createCriteria = example.createCriteria();
        createCriteria.andAddtimeEqualTo(date);
        createCriteria.andPeriodsEqualTo(periods);
        createCriteria.andSourceEqualTo(source);
        long result = -1;
        try {
            result = infoMapper.countByExample(example);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("监听器销毁");
    }

}
