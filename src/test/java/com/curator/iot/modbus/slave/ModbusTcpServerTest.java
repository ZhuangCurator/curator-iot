package com.curator.iot.modbus.slave;

import com.curator.iot.modbus.core.ModbusServer;
import com.curator.iot.modbus.tcp.ModbusTcpServer;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jun
 * @date 2025/2/20 
 */
public class ModbusTcpServerTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    ModbusServer server;

    @Before
    public void setUp() {
        server = new ModbusTcpServer();
        server.start();
    }

    @Test
    public void readCoils() {

    }

}
