package com.curator.iot.modbus.core.msg.response;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import io.netty.buffer.ByteBuf;

/**
 * 写单个线圈响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteSingleCoilResponse extends ModbusResponse {
    private int address;
    private boolean value;

    public WriteSingleCoilResponse() {
        super();
    }

    public WriteSingleCoilResponse(byte unitId, int address, boolean value) {
        super(unitId, ModbusFunctionCode.WRITE_SINGLE_REGISTER);
        this.address = address;
        this.value = value;
    }

    public int getAddress() {
        return address;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.address = in.readUnsignedShort();
        int value = in.readUnsignedShort();
        this.value = value != 0;
        setFunctionCode(ModbusFunctionCode.WRITE_SINGLE_REGISTER);
        setUnitId(unitId);
        setTransactionId(transactionId);
    }
}
