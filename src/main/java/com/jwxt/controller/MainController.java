package com.jwxt.controller;

import com.jwxt.service.GetResult;
import com.jwxt.service.ILogInService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author me@nitmali.com
 * @date 2018/6/4 14:34
 */

@RestController
public class MainController {
    @Resource
    private ILogInService login;

    @Resource
    private GetResult getResult;

    @GetMapping("/login")
    public String systemLogin(HttpServletRequest request, String userId, String password) throws Exception {
        String loginMessage = null; 
        String verificationError = "验证码不正确";
        int maxLogin = 3;
        for (int numberOfLogin = 0; numberOfLogin < maxLogin; numberOfLogin++) {
            if (numberOfLogin > 0) {
                System.out.println("第" + numberOfLogin + "次尝试重新登陆");
            }
            loginMessage = login.getLogin(request, userId, password);
            if (!loginMessage.contains(verificationError)) {
                break;
            }
        }

        return loginMessage;
    }

    @GetMapping("/getResult")
    public String getResult(HttpServletRequest request) {


        try {
            return getResult.getResult(request);
        } catch (Exception e) {
            return "请使用 /login?userId=学号&password=密码 登录";
        }
    }

    @GetMapping("/getResultDirect")
    public String getResult(HttpServletRequest request, String userId, String password) throws Exception {
        String loginMessage = systemLogin(request, userId, password);

        String error = "错误";

        if (!loginMessage.contains(error)) {
            return getResult(request);
        } else {
            return loginMessage;
        }

    }

    @GetMapping("/getClassFrom")
    public String getClassFrom(){
        
        return "getClassFrom";
    }


}
