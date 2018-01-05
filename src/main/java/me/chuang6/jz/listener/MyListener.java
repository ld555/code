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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import me.chuang6.jz.bean.Info;
import me.chuang6.jz.bean.InfoExample;
import me.chuang6.jz.bean.InfoExample.Criteria;
import me.chuang6.jz.dao.InfoMapper;
import me.chuang6.jz.util.PushUtils;
import me.chuang6.jz.util.TimeUtils;

public class MyListener implements ServletContextListener {
	private static final int PERIOD_TIME = 1000 * 30;
	private static final String URL = "http://caipiao.163.com/award/cqssc/%s.html";

	private InfoMapper infoMapper;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
		infoMapper = ctx.getBean(InfoMapper.class);
		Timer timer = new Timer();
		Timer timer2 = new Timer();
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

				if (getCount(info.getPeriods(), date) == 0) {
					// 插入
					info.setAddtime(date);
					infoMapper.insert(info);
					PushUtils.broadcastAll("时时彩助手", info.toString(), info.toString(), 0);

				} else {
					break;// 倒着判断，只要有一个存在，那么前面的肯定也都存在 所以直接跳出循环
				}
			}
		}

	}

	private long getCount(Integer periods, Date date) {
		InfoExample example = new InfoExample();
		Criteria createCriteria = example.createCriteria();
		createCriteria.andAddtimeEqualTo(date);
		createCriteria.andPeriodsEqualTo(periods);
		return infoMapper.countByExample(example);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("==================contextDestroyed");
	}

}
