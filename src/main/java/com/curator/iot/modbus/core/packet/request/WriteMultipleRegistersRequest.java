package com.curator.iot.modbus.core.packet.request;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

/**
 * 写多个寄存器 (0x10)
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteMultipleRegistersRequest extends ModbusRequest {
    private int startAddress;
    private int[] values;

    public WriteMultipleRegistersRequest() {
        super();
    }

    public WriteMultipleRegistersRequest(byte unitId, int startAddress, int[] values) {
        super(unitId, ModbusFunctionCode.WRITE_MULTIPLE_REGISTERS);
        setStartAddress(startAddress);
        setValues(values);
    }

    public void setStartAddress(int startAddress) {
        if (startAddress < 0 || startAddress > 0xFFFF) {
            throw new IllegalArgumentException("Invalid address");
        }
        this.startAddress = startAddress;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setValues(int[] values) {
        if (values == null || values.length == 0 || values.length > 123) {
            throw new IllegalArgumentException("Invalid values array");
        }
        this.values = values.clone();
    }

    public int[] getValues() {
        return values;
    }

    public int getQuantity() {
        return values.length;
    }

    public int getByteCount() {
        return values.length * 2;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.startAddress = in.readUnsignedShort();
        int quantity = in.readUnsignedShort();
        this.values = new int[quantity];
        int byteCount = in.readUnsignedByte();
        for (int i = 0; i < byteCount / 2; i++) {
            values[i] = in.readUnsignedShort();
        }
        setUnitId(unitId);
        setTransactionId(transactionId);
    }

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(6 + getByteCount());
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeShort(startAddress);
            out.writeShort(getQuantity());
            out.writeByte(getByteCount());
            for (int value : values) {
                out.writeShort(value);
            }
            return out.array();
        } finally {
            out.release();
        }
    }

    @Override
    public String toString() {
        return "WriteMultipleRegistersRequest{" +
                "transactionId=" + getTransactionId() +
                ", unit=" + getUnitId() +
                ", functionCode=" + getFunctionCode() +
                ", startAddress=" + startAddress +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
