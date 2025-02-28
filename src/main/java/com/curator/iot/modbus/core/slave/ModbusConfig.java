package com.curator.iot.modbus.core.slave;

/**
 * @author Jun
 * @date 2025/2/26 
 */
public class ModbusConfig {
    private final int port;
    private final int slaveId;

    private final ModbusDataHandler dataHandler;

    public ModbusConfig(int port, int slaveId) {
       this(port, slaveId, new DefaultDataHandler());
    }

    public ModbusConfig(int port, int slaveId, ModbusDataHandler dataHandler) {
        this.port = port;
        this.slaveId = slaveId;
        this.dataHandler = dataHandler;
    }

    public int getPort() {
        return port;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public ModbusDataHandler getDataHandler() {
        return dataHandler;
    }

    public static ModbusConfig defaultConfig() {
        return new ModbusConfig(502, 1, new DefaultDataHandler());
    }
}
