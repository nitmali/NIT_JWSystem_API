package com.jwxt.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * @author nitmali@126.com
 * @date 2018/6/6 20:47
 */

@Service
public class GetResult {
    public String getResult(HttpServletRequest request) throws IOException {

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
                                + "&gnmkdm=N121605"
                )
                .method(Connection.Method.GET)
                .header("Referer", "http://jwxt.nit.net.cn/xs_main.aspx?xh="
                        + session.getAttribute("userId"))
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

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
                                        + "&gnmkdm=N121605"
                        )
                .method(Connection.Method.POST)
                .header("Referer", "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                        + session.getAttribute("userId")
                        + "&xm="
                        + session.getAttribute("userName")
                        + "&gnmkdm=N121605")
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

        return lncjResponse.body();

    }
}
