package com.jwxt.controller;

import com.jwxt.service.Login;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;


/**
 * @author nitmali@126.com
 * @date 2018/6/4 14:34
 */

@RestController
public class MainController {
    @Resource
    private Login login;

    @GetMapping("/getResult")
    public String jwxtLogin(String userId, String password) throws Exception {

        Map<String, String> userInfo = login.getLogin(userId, password);

        Map<String, String> loginPageCookies = login.getLoginPageCookies();

        if ("用户名不存在或未按照要求参加教学活动".equals(userInfo.get("userName"))) {
            return userInfo.get("userName");
        } else if("验证码不正确".equals(userInfo.get("userName"))) {
            return userInfo.get("userName") + " 请重试";
        }else if("密码错误".equals(userInfo.get("userName"))){
            return userInfo.get("userName");
        }

        System.out.println(userInfo.get("userName")+" 登录于 "+new Date());

        Connection.Response cjResponse = Jsoup.connect(
                "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                        + userInfo.get("userId") + "&xm=" + userInfo.get("userName") + "&gnmkdm=N121605"
        )
                .method(Connection.Method.GET)
                .header("Referer", "http://jwxt.nit.net.cn/xs_main.aspx?xh=" + userInfo.get("userId"))
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        String VIEWSTATE = Jsoup.parse(cjResponse.body())
                .getElementsByTag("input")
                .get(2).attr("value");

        Connection.Response lncjResponse = Jsoup.connect(
                "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                        + userInfo.get("userId") + "&xm=" + userInfo.get("userName") + "&gnmkdm=N121605"
        )

                .method(Connection.Method.POST)
                .header("Referer", "http://jwxt.nit.net.cn/xscjcx.aspx?xh="
                        + userInfo.get("userId") + "&xm=" + userInfo.get("userName") + "&gnmkdm=N121605")
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
                .ignoreContentType(true)
                .execute();

        return lncjResponse.body();

    }
}
