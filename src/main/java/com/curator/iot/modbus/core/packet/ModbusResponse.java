package com.curator.iot.modbus.core.packet;

import com.curator.iot.modbus.core.ModbusErrorCode;
import com.curator.iot.modbus.core.ModbusFunctionCode;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public abstract class ModbusResponse extends ModbusPacket{

    public ModbusResponse() {
    }

    public ModbusResponse(byte unitId, ModbusFunctionCode functionCode) {
        super(unitId, functionCode);
    }

    public ModbusResponse(byte unitId, ModbusFunctionCode functionCode, ModbusErrorCode errorCode) {
        super(unitId, functionCode, errorCode);
    }

    /**
     * 异常响应判断
     *
     * @return boolean
     */
    public boolean isException() {
        return (getErrorCode() != null);
    }

}
