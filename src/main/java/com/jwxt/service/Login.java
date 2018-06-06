package com.jwxt.service;

import com.jwxt.service.graphiccr.GetVerification;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
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

    public String  getLogin(HttpServletRequest request, String userId, String password) throws Exception {

        password = password.replace(" ","+");

        HttpSession session = request.getSession();

        session.setAttribute("userId", userId);

        String chrome = "Mozilla/5.0 (Windows NT 10.0; WOW64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) " +
                "Chrome/64.0.3282.186 Safari/537.36";

        Connection.Response fistResponse = Jsoup
                .connect("http://jwxt.nit.net.cn/default2.aspx")
                .method(Connection.Method.GET)
                .userAgent(chrome)
                .execute();

        Map<String, String> loginPageCookies = fistResponse.cookies();


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

        if (txtSecretCodeFile.delete()) {
            System.out.println("验证码识别完成");
        }

        Map<String, String> loginInfo = new HashMap<>();

        loginInfo.put("__VIEWSTATE", VIEWSTATE);
        loginInfo.put("txtUserName", userId);
        loginInfo.put("Textbox1", "");
        loginInfo.put("TextBox2", password);
        loginInfo.put("txtSecretCode", txtSecretCode);
        loginInfo.put("RadioButtonList1", "学生");
        loginInfo.put("Button1", "");
        loginInfo.put("lbLanguage", "");
        loginInfo.put("hidPdrs", "");
        loginInfo.put("hidsc", "");

        Connection.Response loginResponse = Jsoup
                .connect("http://jwxt.nit.net.cn/default2.aspx")
                .method(Connection.Method.POST)
                .data(loginInfo)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        String verificationError = "验证码不正确";
        String userIdError = "用户名不存在或未按照要求参加教学活动";
        String passwordError = "密码错误";

        if (loginResponse.body().contains(userIdError)) {
            session.setAttribute("errorMessage", userIdError);
        } else if (loginResponse.body().contains(verificationError)) {
            session.setAttribute("errorMessage", verificationError);
        } else if (loginResponse.body().contains(passwordError)) {
            session.setAttribute("errorMessage", passwordError);
        }else {
            session.setAttribute("errorMessage", null);
        }

        try {
            String userName = Jsoup.parse(loginResponse.body())
                    .getElementById("xhxm")
                    .text();

            userName = userName.substring(0, userName.length() - 2);

            session.setAttribute("userName", userName);

            session.setAttribute("loginPageCookies", loginPageCookies);

            System.out.println(session.getAttribute("userId")
                    + "  " + session.getAttribute("userName")
                    + " 登录于 " + new Date());
            session.setAttribute("login","true");
            return session.getAttribute("userName").toString();
        } catch (Exception e) {
            System.err.println(session.getAttribute("errorMessage").toString() + "  " + new Date());
            return session.getAttribute("errorMessage").toString();
        }

    }

}
