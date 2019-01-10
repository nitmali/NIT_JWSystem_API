package com.jwxt.controller;

import com.jwxt.bean.Response;
import com.jwxt.service.GetResult;
import com.jwxt.service.ILogInService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author me@nitmali.com
 * @date 2018/6/4 14:34
 */

@CrossOrigin
@RestController
public class ServiceController {
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
    public Response getResult(HttpServletRequest request, String key) {

        try {
            return new Response().success(getResult.getResult(request,key));
        } catch (Exception e) {
            return new Response().failure("请使用 /login?userId=学号&password=密码 登录", 201);
        }
    }

    @GetMapping("/getResultDirect")
    public Response getResult(HttpServletRequest request, String userId, String password, String key) throws Exception {
        String loginMessage = systemLogin(request, userId, password);

        String error = "错误";

        if (!loginMessage.contains(error)) {
            try {
                return new Response().success(getResult.getResult(request, key));
            } catch (Exception e) {
                return new Response().failure("请使用 /login?userId=学号&password=密码 登录", 201);
            }
        } else {
            return new Response().success(loginMessage);
        }

    }

    @GetMapping("/getClassFrom")
    public String getClassFrom(){

        return "getClassFrom";
    }


}
