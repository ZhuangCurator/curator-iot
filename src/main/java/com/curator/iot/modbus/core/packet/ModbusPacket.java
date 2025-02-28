package com.curator.iot.modbus.core.packet;

import com.curator.iot.modbus.core.ModbusErrorCode;
import com.curator.iot.modbus.core.ModbusFunctionCode;
import io.netty.buffer.ByteBuf;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public abstract class ModbusPacket {

    private int transactionId;
    /** 从机ID */
    private byte unitId;
    /** 功能码 */
    private ModbusFunctionCode functionCode;
    /** 错误码 */
    private ModbusErrorCode errorCode;

    public ModbusPacket() {
    }

    public ModbusPacket(byte unitId, ModbusFunctionCode functionCode) {
        this.unitId = unitId;
        this.functionCode = functionCode;
    }

    public ModbusPacket(byte unitId, ModbusFunctionCode functionCode, ModbusErrorCode errorCode) {
        this.unitId = unitId;
        this.functionCode = functionCode;
        this.errorCode = errorCode;
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

    public ModbusErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ModbusErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * 反序列化
     *
     * @param unitId id
     * @param transactionId id
     * @param in 输入
     */
    public abstract void fromBytes(byte unitId, int transactionId, ByteBuf in);

    /**
     * 序列化
     *
     * @return {@link byte[] }
     */
    public abstract byte[] toBytes();
}
