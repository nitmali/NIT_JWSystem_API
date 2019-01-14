package com.jwxt.service.imbl;

import com.jwxt.service.IGetClassScheduleService;
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
    public List<Map<String, String>> getClassSchedule(HttpServletRequest request, String year, String yearNumber) throws IOException {


        HttpSession session = request.getSession();

        Map<String, String> loginPageCookies = (Map<String, String>) session.getAttribute("loginPageCookies");

        Connection.Response classScheduleResponse = Jsoup.connect
                (
                        "http://jwxt.nit.net.cn/xskbcx.aspx?"
                                + "xh=" + session.getAttribute("userId")
                                + "&xm=" + session.getAttribute("userName")
                                + "&gnmkdm=N121603"
                )
                .method(Connection.Method.GET)
                .header("Referer", "http://jwxt.nit.net.cn/xs_main.aspx?xh=" + session.getAttribute("userId"))
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        log.info(session.getAttribute("userId")
                + "  " + session.getAttribute("userName")
                + " 于 " + new Date() + " 查询课表 ");

        Document classScheduleDocument = Jsoup.parse(classScheduleResponse.body());

        String className = classScheduleDocument.getElementById("Label9").text().substring(4);

        Map<String, String> userMap = new HashMap<>();

        userMap.put("userId", session.getAttribute("userId").toString());
        userMap.put("name", session.getAttribute("userName").toString());
        userMap.put("className", className);

        List<Map<String, String>> mapList = new ArrayList<>();

        mapList.add(userMap);


        Elements getTable = classScheduleDocument.select("#Table1");

        Elements trs = getTable.select("tr");

        for (Element tr : trs) {

        }

        return mapList;
    }
}
