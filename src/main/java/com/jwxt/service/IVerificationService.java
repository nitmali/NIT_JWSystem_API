package com.jwxt.service;

import java.io.IOException;
import java.util.Map;

/**
 * @author me@nitmali.com
 * @date 2018/12/14 15:40
 */
public interface IVerificationService {

    /**
     * 获取登陆时验证码
     * @param loginPageCookies 登陆cookies
     * @return 验证码
     * @throws IOException IO异常
     */
    String getVerificationCode(Map<String, String> loginPageCookies) throws IOException;

}
