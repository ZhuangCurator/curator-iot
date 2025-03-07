package com.curator.iot.modbus.core.packet.response;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 写单个寄存器响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteSingleRegisterResponse extends ModbusResponse {
    private int address;
    private int value;

    public WriteSingleRegisterResponse() {
        super();
    }

    public WriteSingleRegisterResponse(byte unitId, int address, int value) {
        super(unitId, ModbusFunctionCode.WRITE_SINGLE_REGISTER);
        this.address = address;
        this.value = value;
    }

    public int getAddress() {
        return address;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.address = in.readUnsignedShort();
        this.value = in.readUnsignedShort();
        setFunctionCode(ModbusFunctionCode.WRITE_SINGLE_REGISTER);
        setUnitId(unitId);
        setTransactionId(transactionId);
    }

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(5);
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeShort(address);
            out.writeShort(value);
            return out.array();
        } finally {
            out.release();
        }
    }
}
