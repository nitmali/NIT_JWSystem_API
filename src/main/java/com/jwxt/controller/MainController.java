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
        return login.getLogin(request, userId, password);
    }

    @GetMapping("/getResult")
    public String getResult(HttpServletRequest request) throws Exception {

//        if (request.getSession().getAttribute("login") == null){
//            login.getLogin(request, userId, password);
//        }
        return  getResult.getResult(request);

    }
}
