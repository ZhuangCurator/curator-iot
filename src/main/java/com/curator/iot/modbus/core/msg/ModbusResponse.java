package com.curator.iot.modbus.core.msg;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import io.netty.buffer.ByteBuf;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public abstract class ModbusResponse extends ModbusPacket{

    private byte exceptionCode;

    public ModbusResponse() {
    }

    public ModbusResponse(byte unitId, ModbusFunctionCode functionCode) {
        super(unitId, functionCode);
    }

    public byte getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(byte exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    /**
     * 异常响应判断
     *
     * @return boolean
     */
    public boolean isException() {
        return exceptionCode != 0;
    }

    /**
     * 反序列化
     *
     * @param unitId id
     * @param transactionId id
     * @param in 输入
     */
    public abstract void fromBytes(byte unitId, int transactionId, ByteBuf in);

}
