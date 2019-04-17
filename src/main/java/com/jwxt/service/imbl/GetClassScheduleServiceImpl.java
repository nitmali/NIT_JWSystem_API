package com.jwxt.service.imbl;

import com.jwxt.service.IGetClassScheduleService;
import com.jwxt.viewModel.ClassScheduleVo;
import com.jwxt.viewModel.ClassVo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * @author me@nitmali.com
 * @date 2019/1/13 23:33
 */
@Service
@Slf4j
public class GetClassScheduleServiceImpl implements IGetClassScheduleService {
    @Override
    public ClassScheduleVo getClassSchedule(HttpServletRequest request, String year, String yearNumber) throws IOException {


        HttpSession session = request.getSession();

        Map<String, String> loginPageCookies = (Map<String, String>) session.getAttribute("loginPageCookies");

        String url = "http://jwxt.nit.net.cn/xskbcx.aspx?"
                + "xh=" + session.getAttribute("userId")
                + "&xm=" + session.getAttribute("userName")
                + "&gnmkdm=N121603";

        Connection.Response firstClassScheduleResponse = Jsoup
                .connect(url)
                .method(Connection.Method.GET)
                .header("Referer", "http://jwxt.nit.net.cn/xs_main.aspx?xh=" + session.getAttribute("userId"))
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        Document classScheduleDocument = Jsoup.parse(firstClassScheduleResponse.body());
        if (year != null && yearNumber != null) {

            Map<String, String> data = new HashMap<>();
            data.put("xnd", year);
            data.put("xqd", yearNumber);
            data.put("__EVENTTARGET", "xnd");
            data.put("__EVENTARGUMENT", "");
            data.put("__VIEWSTATE", classScheduleDocument.select("input").get(2).val());


            Connection.Response classScheduleResponse = Jsoup
                    .connect(url)
                    .method(Connection.Method.POST)
                    .header("Referer", url)
                    .data(data)
                    .cookies(loginPageCookies)
                    .ignoreContentType(true)
                    .execute();
            classScheduleDocument = Jsoup.parse(classScheduleResponse.body());
        }

        log.info(session.getAttribute("userId")
                + "  " + session.getAttribute("userName")
                + " 于 " + new Date() + " 查询课表 ");


        String className = classScheduleDocument.getElementById("Label9").text().substring(4);

        ClassScheduleVo classScheduleVo = new ClassScheduleVo();

        classScheduleVo.setName(session.getAttribute("userName").toString());
        classScheduleVo.setUserId(session.getAttribute("userId").toString());
        classScheduleVo.setClassName(className);


        Elements getTable = classScheduleDocument.select("#Table1");

        Elements trs = getTable.select("tr");

        for (Element tr : trs) {
            for (Element td : tr.select("td")) {
                String text = td.text();
                if (text != null) {
                    if (text.contains("周一")) {
                        classScheduleVo.getMonday().add(getClassData(text));
                    }
                    if (text.contains("周二")) {
                        classScheduleVo.getTuesday().add(getClassData(text));
                    }
                    if (text.contains("周三")) {
                        classScheduleVo.getWednesday().add(getClassData(text));
                    }
                    if (text.contains("周四")) {
                        classScheduleVo.getThursday().add(getClassData(text));
                    }
                    if (text.contains("周五")) {
                        classScheduleVo.getFriday().add(getClassData(text));
                    }
                    if (text.contains("周六")) {
                        classScheduleVo.getSaturday().add(getClassData(text));
                    }
                    if (text.contains("周日")) {
                        classScheduleVo.getSunday().add(getClassData(text));
                    }
                }
            }
        }

        System.out.println(classScheduleVo);
        return classScheduleVo;
    }

    private ClassVo getClassData(String test) {
        List<String> list = Arrays.asList(" ".split(test));
        return new ClassVo(list.get(0), list.get(1), list.get(2), list.get(3));
    }
}
