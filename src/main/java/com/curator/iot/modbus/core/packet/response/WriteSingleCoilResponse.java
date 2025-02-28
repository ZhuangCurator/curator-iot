package com.curator.iot.modbus.core.packet.response;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
        super(unitId, ModbusFunctionCode.WRITE_SINGLE_COIL);
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

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(5);
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeShort(address);
            out.writeShort(value ? 0xFF00 : 0x0000);
            return out.array();
        } finally {
            out.release();
        }
    }
}
