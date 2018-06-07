package com.jwxt.controller;

import com.jwxt.service.GetResult;
import com.jwxt.service.Login;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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

    @Resource
    private GetResult getResult;

    @GetMapping("/login")
    public String jwxtLogin(HttpServletRequest request, String userId, String password) throws Exception{
        String loginMessage = null;

        for (int numberOfLogin = 0;numberOfLogin < 3;numberOfLogin++){
            loginMessage =  login.getLogin(request, userId, password);
            if(numberOfLogin > 0){
                System.out.println("第"+numberOfLogin+"次尝试重新登陆");
            }
            if(!loginMessage.equals("验证码不正确")){
                break;
            }
        }

        return loginMessage;
    }

    @GetMapping("/getResult")
    public String getResult(HttpServletRequest request) throws Exception {

        try {
            return  getResult.getResult(request);
        }catch (Exception e){
            return "请使用 /login?userId=userId&password=password 登录";
        }
    }
}
