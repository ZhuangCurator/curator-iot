package com.curator.iot.modbus.example.tcp;

import com.curator.iot.modbus.core.packet.response.ReadCoilsResponse;
import com.curator.iot.modbus.core.packet.response.ReadRegistersResponse;
import com.curator.iot.modbus.core.ModbusClient;
import com.curator.iot.modbus.tcp.ModbusTcpClient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jun
 * @date 2025/2/27 
 */
public class ModbusTcpClientExample {

    private static final Logger logger = LoggerFactory.getLogger(ModbusTcpClientExample.class);

    public static void main(String[] args) {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        // 设置 root 日志等级为 INFO
        config.getRootLogger().setLevel(Level.INFO);
        ctx.updateLoggers();


        ModbusClient client = new ModbusTcpClient();
        try {
            client.connect("127.0.0.1", 503).get();

            readCoils(client, 0, 4);
            readDiscreteInputs(client, 0, 4);
            readHoldingRegisters(client, 0, 4);
            readInputRegisters(client, 0, 4);

        } catch (Exception e) {
            logger.error("ModbusRtuClient Connect Error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    static void readCoils(ModbusClient client, int startAddress, int quantity) {
        ReadCoilsResponse readCoilsResponse = client.readCoils(1, startAddress, quantity);
        for (int i = 0; i < 4; i++) {
            logger.error("readCoils [address: {}, val: {}]", i, readCoilsResponse.getCoilStatus(i));
        }
    }

    static void readDiscreteInputs(ModbusClient client, int startAddress, int quantity) {
        ReadCoilsResponse readCoilsResponse = client.readDiscreteInputs(1, startAddress, quantity);
        for (int i = 0; i < 4; i++) {
            logger.error("readDiscreteInputs [address: {}, val: {}]", i, readCoilsResponse.getCoilStatus(i));
        }
    }

    static void readHoldingRegisters(ModbusClient client, int startAddress, int quantity) {
        ReadRegistersResponse readRegistersResponse = client.readHoldingRegisters(1, startAddress, quantity);
        for (int i = 0; i < 4; i++) {
            logger.error("readHoldingRegisters [address: {}, val: {}]", i, readRegistersResponse.getRegisters()[i]);
        }
    }

    static void readInputRegisters(ModbusClient client, int startAddress, int quantity) {
        ReadRegistersResponse readRegistersResponse = client.readInputRegisters(1, startAddress, quantity);
        for (int i = 0; i < 4; i++) {
            logger.error("readInputRegisters [address: {}, val: {}]", i, readRegistersResponse.getRegisters()[i]);
        }
    }
}
