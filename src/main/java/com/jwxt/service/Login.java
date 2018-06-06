package com.jwxt.service;

import com.jwxt.service.graphiccr.GetVerification;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author nitmali@126.com
 * @date 2018/6/6 14:27
 */

@Service
public class Login {

    @Resource
    private GetVerification getVerification;

    private Map<String, String> loginPageCookies;

    public Map<String, String> getLogin(String userId, String password) throws Exception {
        String loginInfo = doLogin(userId, password);
        if (loginInfo != null) {
            Map<String, String> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("userName", loginInfo);
            return userInfo;
        } else {
            return null;
        }
    }

    private String doLogin(String userId, String password) throws Exception {

        String chrome = "Mozilla/5.0 (Windows NT 10.0; WOW64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/64.0.3282.186 Safari/537.36";

        Connection.Response fistResponse = Jsoup
                .connect("http://jwxt.nit.net.cn/default2.aspx")
                .method(Connection.Method.GET)
                .userAgent(chrome)
                .execute();

        loginPageCookies = fistResponse.cookies();

        String VIEWSTATE = Jsoup.parse(fistResponse.body())
                .getElementsByTag("input")
                .get(0).attr("value");


        Connection.Response txtSecretCodeResponse = Jsoup
                .connect("http://jwxt.nit.net.cn/CheckCode.aspx")
                .method(Connection.Method.GET)
                .userAgent(chrome)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        byte[] gif = txtSecretCodeResponse.bodyAsBytes();

        InputStream input = new ByteArrayInputStream(gif);

        Long fileName = System.currentTimeMillis();

        GetVerification.savaImage(gif, getVerification.getVerificationPath(), fileName + ".gif");

        File txtSecretCodeFile = new File(getVerification.getVerificationPath() + fileName + ".gif");

        String txtSecretCode = getVerification.getVerification(txtSecretCodeFile);

        if(txtSecretCodeFile.delete()){
            System.out.println("验证码识别完成");
        }

        Map<String, String> loginInfo = new HashMap<>();

        loginInfo.put("__VIEWSTATE", VIEWSTATE);
        loginInfo.put("txtUserName", userId);
        loginInfo.put("Textbox1", "");
        loginInfo.put("TextBox2", password);
        loginInfo.put("txtSecretCode", txtSecretCode);
        loginInfo.put("RadioButtonList1", "%D1%A7%C9%FA");
        loginInfo.put("Button1", "");
        loginInfo.put("lbLanguage", "");
        loginInfo.put("hidPdrs", "");
        loginInfo.put("hidsc", "");


        Connection.Response loginResponse = Jsoup.connect("http://jwxt.nit.net.cn/default2.aspx")
                .method(Connection.Method.POST)
                .data(loginInfo)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();


        if (loginResponse.body().contains("用户名不存在或未按照要求参加教学活动")) {
            return "用户名不存在或未按照要求参加教学活动";
        } else if (loginResponse.body().contains("验证码不正确")) {
            return "验证码不正确";

        } else if (loginResponse.body().contains("密码错误")) {
            return "密码错误";
        }
        String userName = Jsoup.parse(loginResponse.body())
                .getElementById("xhxm")
                .text();

        userName = userName.substring(0, userName.length() - 2);

        return userName;
    }

    public Map<String, String> getLoginPageCookies() {
        return loginPageCookies;
    }
}
