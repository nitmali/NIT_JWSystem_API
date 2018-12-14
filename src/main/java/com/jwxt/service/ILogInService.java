package com.jwxt.service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 教务系统登陆服务
 *
 * @author me@nitmali.com
 * @date 2018/12/14 15:21
 */
public interface ILogInService {

    /**
     * 教务系统登陆
     * @param httpServletRequest httpServletRequest
     * @param userId 学号
     * @param password 密码
     * @return 姓名
     * @throws IOException IO异常
     */
    String getLogin(HttpServletRequest httpServletRequest, String userId, String password) throws IOException;

}
