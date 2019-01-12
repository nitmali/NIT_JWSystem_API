package com.jwxt.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author me@nitmali.com
 * @date 2018/6/7 15:23
 */
@ConfigurationProperties(prefix = "verification")
@Service
@Data
public class VerificationConfig {

    private String cachingPath = "caching-path";

    private String errorCachingPath = "error-caching-path";

    private String targetTrainFilePath = "src/main/resources/static/verification/targetTrain.png";

    private String targetPath = "src/main/resources/static/verification/caching";
}
