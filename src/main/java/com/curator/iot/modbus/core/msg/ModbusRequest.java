package com.curator.iot.modbus.core.msg;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.TransactionIdGenerator;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public abstract class ModbusRequest extends ModbusPacket{

    public ModbusRequest(byte unitId, ModbusFunctionCode functionCode) {
        super(unitId, functionCode);
        setTransactionId(TransactionIdGenerator.get());
    }

    /**
     * 序列化
     *
     * @return {@link byte[] }
     */
    public abstract byte[] toBytes();
}
