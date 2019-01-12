package com.jwxt.service.imbl;

import com.jwxt.exception.SysRuntimeException;
import com.jwxt.service.ILogInService;
import com.jwxt.service.IVerificationService;
import com.jwxt.service.Verification.VerificationConfig;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author me@nitmali.com
 * @date 2018/12/14 16:41
 */
@Slf4j
@Service
public class ILoginServiceImpl implements ILogInService{

    public static final String VIEW_STATE = "__VIEWSTATE";

    public static final String TXT_USER_NAME = "txtUserName";
    public static final String TEXT_BOX_1 = "Textbox1";
    public static final String TEXT_BOX_2 = "Textbox2";
    public static final String TXT_SECRET_CODE = "txtSecretCode";
    public static final String RADIO_BUTTON_LIST_1 = "RadioButtonList1";
    public static final String BUTTON_1 = "Button1";
    public static final String LB_LANGUAGE = "lbLanguage";
    public static final String HID_PD_RS = "hidPdrs";
    public static final String HI_DSC = "hidsc";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String VERIFICATION_ERROR = "验证码不正确";
    public static final String USER_ID_ERROR = "用户名不存在或未按照要求参加教学活动";
    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ID_PASSWORD_NULL_ERROR = "学号和密码不得为空";
    public static final String LOGIN_URL = "http://jwxt.nit.net.cn/default2.aspx";
    public static final String GET_VERIFICATION_URL = "http://jwxt.nit.net.cn/CheckCode.aspx";

    @Resource
    private IVerificationService iVerificationService;

    @Resource
    private VerificationConfig verificationConfig;

    @Override
    public String getLogin(HttpServletRequest httpServletRequest, String userId, String password) throws IOException {


        HttpSession session = httpServletRequest.getSession();

        if (userId == null || password == null || "".equals(userId) || "".equals(password)) {
            log.error("错误：" + ID_PASSWORD_NULL_ERROR);
            throw new SysRuntimeException("错误：" + ID_PASSWORD_NULL_ERROR);
        } else {
            password = password.replace(" ", "+");
        }


        Map<String, String> loginPageCookies = null;

        String viewState = null;

        session.setAttribute("userId", userId);


        try {
            Connection.Response fistResponse = Jsoup
                    .connect(LOGIN_URL)
                    .method(Connection.Method.GET)
                    .timeout(5000)
                    .execute();

            loginPageCookies = fistResponse.cookies();

            viewState = Jsoup.parse(fistResponse.body())
                    .getElementsByTag("input")
                    .get(0).attr("value");

        } catch (Exception e) {
            session.setAttribute(ERROR_MESSAGE, "错误：登陆失败，请稍后再试");
            log.error(session.getAttribute(ERROR_MESSAGE).toString() + "  " + new Date());
            return session.getAttribute(ERROR_MESSAGE).toString();
        }


        String txtSecretCode = iVerificationService.getVerificationCode(loginPageCookies);


        Map<String, String> loginInfo = getLoginInfoMap(userId, password, viewState, txtSecretCode);

        Connection.Response loginResponse = Jsoup
                .connect(LOGIN_URL)
                .method(Connection.Method.POST)
                .data(loginInfo)
                .cookies(loginPageCookies)
                .ignoreContentType(true)
                .execute();

        if (loginResponse.body().contains(USER_ID_ERROR)) {
            log.error("错误：" + USER_ID_ERROR);
            throw new SysRuntimeException("错误：" + USER_ID_ERROR);
        } else if (loginResponse.body().contains(VERIFICATION_ERROR)) {
            VerificationServiceImpl.saveImage(VerificationServiceImpl.getGif,verificationConfig.getErrorCachingPath(), txtSecretCode + ".gif");
            session.setAttribute(ERROR_MESSAGE, "错误：" + VERIFICATION_ERROR);
        } else if (loginResponse.body().contains(PASSWORD_ERROR)) {
            log.error("错误：" + PASSWORD_ERROR);
            throw new SysRuntimeException("错误：" + PASSWORD_ERROR);
        } else {
            VerificationServiceImpl.saveImage(VerificationServiceImpl.getGif,verificationConfig.getCachingPath(), txtSecretCode + ".gif");
            session.setAttribute(ERROR_MESSAGE, null);
        }

        if (session.getAttribute(ERROR_MESSAGE) == null) {
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
                session.setAttribute(ERROR_MESSAGE, "otherError");
                log.error(session.getAttribute(ERROR_MESSAGE).toString() + "  " + new Date());
                return session.getAttribute(ERROR_MESSAGE).toString();
            }
        } else {
            log.error(session.getAttribute(ERROR_MESSAGE).toString() + "  " + new Date());
            return session.getAttribute(ERROR_MESSAGE).toString();
        }
    }

    private Map<String, String> getLoginInfoMap(String userId, String password, String viewState, String txtSecretCode) {
        Map<String, String> loginInfo = new HashMap<>();
        loginInfo.put(VIEW_STATE, viewState);
        loginInfo.put(TXT_USER_NAME, userId);
        loginInfo.put(TEXT_BOX_1, "");
        loginInfo.put(TEXT_BOX_2, password);
        loginInfo.put(TXT_SECRET_CODE, txtSecretCode);
        loginInfo.put(RADIO_BUTTON_LIST_1, "学生");
        loginInfo.put(BUTTON_1, "");
        loginInfo.put(LB_LANGUAGE, "");
        loginInfo.put(HID_PD_RS, "");
        loginInfo.put(HI_DSC, "");
        return loginInfo;
    }

}
