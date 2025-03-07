package com.curator.iot.modbus.core.packet.request;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 写单个寄存器 (0x06)
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteSingleRegisterRequest extends ModbusRequest {
    private int address;
    private int value;

    public WriteSingleRegisterRequest() {
        super();
    }

    public WriteSingleRegisterRequest(byte unitId, int address, int value) {
        super(unitId, ModbusFunctionCode.WRITE_SINGLE_REGISTER);
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

    public void setValue(int value) {
        if (value < 0 || value > 0xFFFF) {
            throw new IllegalArgumentException("Value out of range");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.address = in.readUnsignedShort();
        this.value = in.readUnsignedShort();
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

    @Override
    public String toString() {
        return "WriteSingleRegisterRequest{" +
                "transactionId=" + getTransactionId() +
                ", unit=" + getUnitId() +
                ", functionCode=" + getFunctionCode() +
                ", address=" + address +
                ", value=" + value +
                '}';
    }
}
