package com.jwxt.service.Verification;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author me@nitmali.com
 * @date 2018/6/7 15:23
 */
@ConfigurationProperties(prefix = "verification")
@Service
public class VerificationConfig {

    private String cachingPath = "caching-path";

    public String getCachingPath() {
        return cachingPath;
    }

    public void setCachingPath(String cachingPath) {
        this.cachingPath = cachingPath;
    }
}
