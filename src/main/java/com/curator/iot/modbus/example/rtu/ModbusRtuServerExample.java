package com.curator.iot.modbus.example.rtu;

import com.curator.iot.modbus.core.ModbusServer;
import com.curator.iot.modbus.rtu.ModbusRtuServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

/**
 * @author Jun
 * @date 2025/2/27 
 */
public class ModbusRtuServerExample {

    public static void main(String[] args) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();
        // 设置 root 日志等级为 INFO
        configuration.getRootLogger().setLevel(Level.INFO);
        ctx.updateLoggers();

        ModbusServer server = new ModbusRtuServer();
        server.start();
    }
}
