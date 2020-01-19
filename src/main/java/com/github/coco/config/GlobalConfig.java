package com.github.coco.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Global configuration
 * You can customize these by setting application.yml
 *
 * @author db1995
 */
@ConfigurationProperties("coco")
@Component
public final class GlobalConfig {
    private static int maxCustomerPerService = 5;
    private static String welcome = "";
    private static String autoReplyAfterWork = "";
    private static String unifiedServiceName = "";
    private static boolean alwaysDisplayUnifiedServiceName = true;
    private static String serviceDown = "";

    public static int getMaxCustomerPerService() {
        return maxCustomerPerService;
    }

    public void setMaxCustomerPerService(int maxCustomerPerService) {
        GlobalConfig.maxCustomerPerService = maxCustomerPerService;
    }

    public static String getWelcome() {
        return welcome;
    }

    public void setWelcome(String welcome) {
        GlobalConfig.welcome = welcome;
    }

    public static String getAutoReplyAfterWork() {
        return autoReplyAfterWork;
    }

    public void setAutoReplyAfterWork(String autoReplyAfterWork) {
        GlobalConfig.autoReplyAfterWork = autoReplyAfterWork;
    }

    public static String getUnifiedServiceName() {
        return unifiedServiceName;
    }

    public void setUnifiedServiceName(String unifiedServiceName) {
        GlobalConfig.unifiedServiceName = unifiedServiceName;
    }

    public static boolean isAlwaysDisplayUnifiedServiceName() {
        return alwaysDisplayUnifiedServiceName;
    }

    public void setAlwaysDisplayUnifiedServiceName(boolean alwaysDisplayUnifiedServiceName) {
        GlobalConfig.alwaysDisplayUnifiedServiceName = alwaysDisplayUnifiedServiceName;
    }

    public static String getServiceDown() {
        return serviceDown;
    }

    public void setServiceDown(String serviceDown) {
        GlobalConfig.serviceDown = serviceDown;
    }
}