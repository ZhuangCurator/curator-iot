package com.curator.iot.modbus.core;

/**
 * 重连配置类
 *
 * @author Jun
 * @date 2025/2/12 
 */
public class ReconnectConfig {

    /** 自动重新连接 */
    private boolean autoReconnect;
    /** 最大重试次数 小于或等于0无限重试 */
    private int maxRetries;
    /** 初始间隔 ms */
    private long initialInterval;
    /** 最大间隔 ms */
    private long maxInterval;
    /** 间隔倍数 */
    private double multiplier;

    public ReconnectConfig(boolean autoReconnect, int maxRetries,
                           long initialInterval, long maxInterval, double multiplier) {
        this.autoReconnect = autoReconnect;
        this.maxRetries = maxRetries;
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;
        this.multiplier = multiplier;
    }

    /**
     * 默认配置 最多重试10次，初始间隔1秒，最大间隔30秒，间隔倍数1.5
     *
     * @return {@link ReconnectConfig }
     */
    public static ReconnectConfig defaultConfig() {
        return new ReconnectConfig(true, 10, 1000, 30000, 1.5);
    }

    public boolean isAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public long getInitialInterval() {
        return initialInterval;
    }

    public void setInitialInterval(long initialInterval) {
        this.initialInterval = initialInterval;
    }

    public long getMaxInterval() {
        return maxInterval;
    }

    public void setMaxInterval(long maxInterval) {
        this.maxInterval = maxInterval;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(double multiplier) {
        this.multiplier = multiplier;
    }
}
