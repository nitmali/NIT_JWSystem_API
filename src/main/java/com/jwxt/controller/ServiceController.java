package com.jwxt.controller;

import com.jwxt.bean.Response;
import com.jwxt.exception.SysRuntimeException;
import com.jwxt.service.IGetClassScheduleService;
import com.jwxt.service.ILogInService;
import com.jwxt.service.imbl.IGetResultServiceImpl;
import com.jwxt.utils.VerificationTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


/**
 * @author me@nitmali.com
 * @date 2018/6/4 14:34
 */

@Slf4j
@CrossOrigin
@RestController
public class ServiceController {
    @Resource
    private VerificationTool verificationTool;

    @Resource
    private ILogInService iLogInService;

    @Resource
    private IGetResultServiceImpl iGetResultService;

    @Resource
    protected IGetClassScheduleService iGetClassScheduleService;

    @GetMapping("/login")
    public Response systemLogin(HttpServletRequest request, String userId, String password) throws Exception {
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
            return new Response().success(loginMessage);
        } else {
            throw new SysRuntimeException("验证码错误请重新登陆");
        }
    }

    @GetMapping("/get-result")
    public Response getResult(HttpServletRequest request, String year, String yearNumber) {

        try {
            return new Response().success(iGetResultService.getResult(request, year, yearNumber));
        } catch (Exception e) {
            throw new SysRuntimeException("请使用 /login?userId=学号&password=密码 登录");
        }
    }

    @GetMapping("/get-result-direct")
    public Response getResult(HttpServletRequest request, String userId, String password, String year, String yearNumber) throws Exception {
        String loginMessage = systemLogin(request, userId, password).getData().toString();

        String error = "错误";

        if (!loginMessage.contains(error)) {
            try {
                return new Response().success(iGetResultService.getResult(request, year, yearNumber));
            } catch (Exception e) {
                throw new SysRuntimeException("请使用 /login?userId=学号&password=密码 登录");
            }
        } else {
            return new Response().success(loginMessage);
        }

    }

    @GetMapping("/get-class-schedule")
    public Response getClassFrom(HttpServletRequest request, String year, String yearNumber) throws IOException {

        return new Response().success(iGetClassScheduleService.getClassSchedule(request, year, yearNumber));
    }

    @GetMapping("/train")
    public Response train() {
        return new Response().success(verificationTool.train());
    }


}
