package com.jwxt.controller;

import com.jwxt.bean.Response;
import com.jwxt.exception.SysRuntimeException;
import com.jwxt.service.imbl.IGetResultServiceImpl;
import com.jwxt.service.ILogInService;
import com.jwxt.service.Verification.GraphicC2Translator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * @author me@nitmali.com
 * @date 2018/6/4 14:34
 */

@Slf4j
@CrossOrigin
@RestController
public class ServiceController {
    @Resource
    private GraphicC2Translator graphicC2Translator;

    @Resource
    private ILogInService iLogInService;

    @Resource
    private IGetResultServiceImpl iGetResultService;

    @GetMapping("/login")
    public String systemLogin(HttpServletRequest request, String userId, String password) throws Exception {
        String loginMessage = null;
        String verificationError = "验证码不正确";
        int maxLogin = 9;
        for (int numberOfLogin = 0; numberOfLogin <= maxLogin; numberOfLogin++) {
            if (numberOfLogin > 0) {
                log.info("第" + numberOfLogin + "次尝试重新登陆");
            }
            loginMessage = iLogInService.getLogin(request, userId, password);
            if (!loginMessage.contains(verificationError)) {
                break;
            }
        }
        if (!loginMessage.contains(verificationError)) {
            return loginMessage;
        } else {
            throw new SysRuntimeException("验证码错误请重新登陆");
        }
    }

    @GetMapping("/getResult")
    public Response getResult(HttpServletRequest request, String key) {

        try {
            return new Response().success(iGetResultService.getResult(request,key));
        } catch (Exception e) {
            throw new SysRuntimeException("请使用 /login?userId=学号&password=密码 登录");
        }
    }

    @GetMapping("/getResultDirect")
    public Response getResult(HttpServletRequest request, String userId, String password, String key) throws Exception {
        String loginMessage = systemLogin(request, userId, password);

        String error = "错误";

        if (!loginMessage.contains(error)) {
            try {
                return new Response().success(iGetResultService.getResult(request, key));
            } catch (Exception e) {
                throw new SysRuntimeException("请使用 /login?userId=学号&password=密码 登录");
            }
        } else {
            return new Response().success(loginMessage);
        }

    }

    @GetMapping("/getClassFrom")
    public String getClassFrom(){

        return "getClassFrom";
    }

    @GetMapping("/train")
    public Response train() {
        return new Response().success(graphicC2Translator.train());
    }


}
