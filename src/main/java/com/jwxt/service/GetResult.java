package com.jwxt.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author nitmali@126.com
 * @date 2j18/6/6 20:47
 */

@Service
public class GetResult {

    public String getResult(HttpServletRequest request) throws IOException, JSONException {

        HttpSession session = request.getSession();

        Map<String, String> loginPageCookies = (Map<String, String>) session.getAttribute("loginPageCookies");

        if (session.getAttribute("errorMessage") != null) {
            return session.getAttribute("errorMessage").toString();
        }


        Connection.Response cjResponse = Jsoup.connect
                (
                        "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                                + session.getAttribute("userId")
                                + "&xm="
                                + session.getAttribute("userName")
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

        className = className.substring(4,className.length());

        String VIEWSTATE = Jsoup.parse(cjResponse.body())
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
                        "__VIEWSTATE", VIEWSTATE,
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

        System.out.println(session.getAttribute("userId")
                + "  " + session.getAttribute("userName")
                + " 于 " + new Date() + " 查询成绩 ");

        Document getPage = Jsoup.parse(lncjResponse.body());

        Elements getTable = getPage.select("#Datagrid1");

        Elements trs = getTable.select("tr");

        JSONArray jsonObjectArray = new JSONArray();
        JSONObject userJson = new JSONObject();

        userJson.put("学号", session.getAttribute("userId"));
        userJson.put("姓名", session.getAttribute("userName"));
        userJson.put("班级",className);

        jsonObjectArray.put(userJson);
        for (int i = 1; i < trs.size(); i++) {
            Elements tds = trs.get(i).select("td");
            JSONObject jsonObject = new JSONObject();

            for (int j = 0; j < tds.size(); j++) {
                jsonObject.put(
                        trs.get(0).select("td").get(j).text(),
                        tds.get(j).text()
                );
            }
            jsonObjectArray.put(jsonObject);
        }

        return jsonObjectArray.toString();
    }

}

