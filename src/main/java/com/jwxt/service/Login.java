package com.jwxt.service;

import com.jwxt.service.Verification.GetVerification;
import com.jwxt.service.Verification.VerificationConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author me@nitmali.com
 * @date 2018/6/6 14:27
 */

@Slf4j
@Service
public class Login {

    private static final String CHECK_URL = "http://jwxt.nit.net.cn/CheckCode.aspx";
    @Resource
    private GetVerification getVerification;


    @Resource
    private VerificationConfig verificationConfig;

    public String getLogin(HttpServletRequest request, String userId, String password) throws Exception {

        HttpSession session = request.getSession();

        String errorMessage = "errorMessage";
        String verificationError = "验证码不正确";
        String userIdError = "用户名不存在或未按照要求参加教学活动";
        String passwordError = "密码错误";
        String nullError = "学号和密码不得为空";

        if(userId == null || password == null || "".equals(userId) || "".equals(password)){
            session.setAttribute(errorMessage,"错误："+nullError);
            System.err.println(session.getAttribute(errorMessage).toString() + "  " + new Date());
            return session.getAttribute(errorMessage).toString();
        }else {
            password = password.replace(" ", "+");
        }


        Map<String, String> loginPageCookies = null;

        String viewstate = null;

        session.setAttribute("userId", userId);

        try {
            Connection.Response fistResponse = Jsoup
                    .connect("http://jwxt.nit.net.cn/default2.aspx")
                    .method(Connection.Method.GET)
                    .timeout(5000)
                    .execute();

            loginPageCookies = fistResponse.cookies();

            viewstate = Jsoup.parse(fistResponse.body())
                    .getElementsByTag("input")
                    .get(0).attr("value");

        } catch (Exception e) {
            session.setAttribute(errorMessage, "错误：登陆失败，请稍后再试");
            System.err.println(session.getAttribute(errorMessage).toString() + "  " + new Date());
            return session.getAttribute(errorMessage).toString();
        }


        Connection.Response txtSecretCodeResponse = Jsoup
                .connect(CHECK_URL)
                .method(Connection.Method.GET)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        byte[] gif = txtSecretCodeResponse.bodyAsBytes();

        long fileName = System.currentTimeMillis();

        GetVerification.saveImage(gif, verificationConfig.getCachingPath(), fileName + ".gif");

        File txtSecretCodeFile = new File(verificationConfig.getCachingPath() + fileName + ".gif");

        String txtSecretCode = getVerification.getVerification(txtSecretCodeFile);

        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put("__VIEWSTATE", viewstate);
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

        if (loginResponse.body().contains(userIdError)) {
            session.setAttribute(errorMessage, "错误："+userIdError);
        } else if (loginResponse.body().contains(verificationError)) {
            session.setAttribute(errorMessage, "错误："+verificationError);
        } else if (loginResponse.body().contains(passwordError)) {
            session.setAttribute(errorMessage, "错误："+passwordError);
        } else {
            session.setAttribute(errorMessage, null);
        }

        if (session.getAttribute(errorMessage) == null){
            try {
                
                String userName = Jsoup.parse(loginResponse.body())
                        .getElementById("xhxm")
                        .text();

                userName = userName.substring(0, userName.length() - 2);

                session.setAttribute("userName", userName);

                session.setAttribute("loginPageCookies", loginPageCookies);

                log.info(session.getAttribute("userId")
                        + "  " + session.getAttribute("userName")
                        + " 登录于 " + new Date());
                session.setAttribute("login", "true");
                return session.getAttribute("userName").toString();
            } catch (Exception e) {
                session.setAttribute(errorMessage,"otherError");
                System.err.println(session.getAttribute(errorMessage).toString() + "  " + new Date());
                return session.getAttribute(errorMessage).toString();
            }
        }else {
            System.err.println(session.getAttribute(errorMessage).toString() + "  " + new Date());
            return session.getAttribute(errorMessage).toString();
        }

    }

}
