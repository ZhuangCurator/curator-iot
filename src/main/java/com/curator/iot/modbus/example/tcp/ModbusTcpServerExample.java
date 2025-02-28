package com.curator.iot.modbus.example.tcp;

import com.curator.iot.modbus.core.slave.ModbusConfig;
import com.curator.iot.modbus.core.ModbusServer;
import com.curator.iot.modbus.tcp.ModbusTcpServer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

/**
 * @author Jun
 * @date 2025/2/27 
 */
public class ModbusTcpServerExample {

    public static void main(String[] args) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = ctx.getConfiguration();
        // 设置 root 日志等级为 INFO
        configuration.getRootLogger().setLevel(Level.INFO);
        ctx.updateLoggers();

        ModbusConfig config = new ModbusConfig(503, 1);
        ModbusServer server = new ModbusTcpServer(config);
        server.start();

//        ModbusDataHandler dataHandler = server.getConfig().getDataHandler();
//        Random random = new Random();

//        while (true) {
//            boolean[] values = new boolean[10];
//            for (int i = 0; i < values.length; i++) {
//                boolean randomBoolean = random.nextInt(10) < 5;
//                values[i] = randomBoolean;
//            }
//            dataHandler.writeCoils(0, values);
//
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
