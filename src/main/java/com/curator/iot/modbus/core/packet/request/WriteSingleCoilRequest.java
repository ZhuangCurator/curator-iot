package com.curator.iot.modbus.core.packet.request;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 写单个线圈 (0x05)
 *
 * @author Jun
 * @date 2025/2/19 
 */
public class WriteSingleCoilRequest extends ModbusRequest {

    private int address;
    private boolean value;

    public WriteSingleCoilRequest() {
        super();
    }

    public WriteSingleCoilRequest(byte unitId, int address, boolean value) {
        super(unitId, ModbusFunctionCode.WRITE_SINGLE_COIL);
        setAddress(address);
        setValue(value);
    }

    public void setAddress(int address) {
        if (address < 0 || address > 0xFFFF) {
            throw new IllegalArgumentException("Invalid address");
        }
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.address = in.readUnsignedShort();
        // 0xFF00 or 0x0000
        int value = in.readUnsignedShort();
        this.value = value != 0;
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

    @Override
    public String toString() {
        return "WriteSingleCoilRequest{" +
                "transactionId=" + getTransactionId() +
                ", unit=" + getUnitId() +
                ", functionCode=" + getFunctionCode() +
                ", address=" + address +
                ", value=" + value +
                '}';
    }
}
