package com.jwxt.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        // 跨域配置
        response.setHeader("Access-Control-Allow-Origin", "*");
        // 允许方法配置
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        // 预检时间间隔30分钟 （http:options）
        response.setHeader("Access-Control-Max-Age", "86400");
        response.setHeader("Access-Control-Expose-Headers", "Refresh-Token");
        // 取消缓存
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store");
        // Http头部信息
        response.setHeader("Access-Control-Allow-Headers",
                "x-requested-with, Api-Ver, Authorization, locale, accept, content-type, x-http-method-override");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void destroy() {
    }
}
