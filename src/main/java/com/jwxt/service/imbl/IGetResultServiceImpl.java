package com.jwxt.service.imbl;

import com.jwxt.exception.SysRuntimeException;
import com.jwxt.service.IGetResultService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

/**
 * @author me@nitmali.com
 * @date 2j18/6/6 20:47
 */

@Slf4j
@Service
public class IGetResultServiceImpl implements IGetResultService {

    @Override
    public List<Map<String, String>> getResult(HttpServletRequest request, String key) throws IOException {

        HttpSession session = request.getSession();

        Map<String, String> loginPageCookies = (Map<String, String>) session.getAttribute("loginPageCookies");

        if (session.getAttribute("errorMessage") != null) {
            throw new SysRuntimeException(session.getAttribute("errorMessage").toString());
        }

        Connection.Response cjResponse = Jsoup.connect
                (
                        "http://jwxt.nit.net.cn/xscjcx.aspx?"
                                + "xh="
                                + session.getAttribute("userId")
//                                + "&xm="
//                                + session.getAttribute("userName")
                                + "&gnmkdm=N1216j5"
                )
                .method(Connection.Method.GET)
                .header("Referer", "http://jwxt.nit.net.cn/xs_main.aspx?xh="
                        + session.getAttribute("userId"))
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        String className = Jsoup.parse(cjResponse.body())
                .getElementById("lbl_xzb").text();

        className = className.substring(4);

        String viewState = Jsoup.parse(cjResponse.body())
                .getElementsByTag("input")
                .get(2).attr("value");

        Connection.Response lncjResponse = Jsoup
                .connect
                        (
                                "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                                        + session.getAttribute("userId")
                                        + "&xm="
                                        + session.getAttribute("userName")
                                        + "&gnmkdm=N1216j5"
                        )
                .method(Connection.Method.POST)
                .header("Referer", "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                        + session.getAttribute("userId")
                        + "&xm="
                        + session.getAttribute("userName")
                        + "&gnmkdm=N1216j5")
                .data("__EVENTTARGET", "",
                        "__EVENTARGUMENT", "",
                        "__VIEWSTATE", viewState,
                        "hidLanguage", "",
                        "ddlXN", "",
                        "ddlXQ", "",
                        "ddl_kcxz", "",
                        "btn_zcj", ""
                )
                .cookies(loginPageCookies)
                .timeout(10000)
                .ignoreContentType(true)
                .execute();

        log.info(session.getAttribute("userId")
                + "  " + session.getAttribute("userName")
                + " 于 " + new Date() + " 查询成绩 ");

        Document getPage = Jsoup.parse(lncjResponse.body());

        Elements getTable = getPage.select("#Datagrid1");

        Elements trs = getTable.select("tr");


        List<Map<String, String>> resultMapList = new ArrayList<>();
        Map<String, String> userMap = new HashMap<>();

        userMap.put("userId", session.getAttribute("userId").toString());
        userMap.put("name", session.getAttribute("userName").toString());
        userMap.put("className", className);

        resultMapList.add(userMap);
        int classSum;
        Double achievementSum = 0d;
        for (classSum = 1; classSum < trs.size(); classSum++) {
            Elements tds = trs.get(classSum).select("td");
            Map<String, String> resultMap = new HashMap<>();

            for (int j = 0; j < tds.size(); j++) {
                resultMap.put(
                        trs.get(0).select("td").get(j).text(),
                        tds.get(j).text()
                );
            }
//            if (key != null && !"".equals(key) && resultMap.get("课程名称").contains(key)) {
//                resultMapList.add(resultMap);
//            }
            if (resultMap.containsKey("绩点")) {
                try {
                    achievementSum += Double.valueOf(resultMap.get("绩点"));
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
            resultMapList.add(resultMap);
        }
        Double achievementAvg = achievementSum /classSum;
        achievementAvg = Math.round(achievementAvg * 100) / 100.0;
        resultMapList.get(0).put("achievementAvg", achievementAvg.toString());
        return resultMapList;
    }

}

