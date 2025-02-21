package com.curator.iot.modbus.core.msg;

import com.curator.iot.modbus.core.ModbusFunctionCode;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusPacket {

    private int transactionId;
    /** 从机ID */
    private byte unitId;
    /** 功能码 */
    private ModbusFunctionCode functionCode;

    public ModbusPacket() {
    }

    public ModbusPacket(byte unitId, ModbusFunctionCode functionCode) {
        this.unitId = unitId;
        this.functionCode = functionCode;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public byte getUnitId() {
        return unitId;
    }

    public void setUnitId(byte unitId) {
        this.unitId = unitId;
    }

    public ModbusFunctionCode getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(ModbusFunctionCode functionCode) {
        this.functionCode = functionCode;
    }
}
