package com.curator.iot.modbus.core.packet.request;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

/**
 * 写多个线圈 (0x0F)
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteMultipleCoilsRequest extends ModbusRequest {

    private int startAddress;
    private boolean[] values;

    public WriteMultipleCoilsRequest() {
        super();
    }

    public WriteMultipleCoilsRequest(byte unitId, int startAddress, boolean[] values) {
        super(unitId, ModbusFunctionCode.WRITE_MULTIPLE_COILS);
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

    public void setValues(boolean[] values) {
        if (values == null || values.length == 0 || values.length > 0x7B0) {
            throw new IllegalArgumentException("Invalid values array");
        }
        this.values = values.clone();
    }

    public boolean[] getValues() {
        return values;
    }

    public int getQuantity() { return values.length; }
    public int getByteCount() { return (int) Math.ceil(values.length / 8.0); }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.startAddress = in.readUnsignedShort();
        int quantity = in.readUnsignedShort();
        this.values = new boolean[quantity];
        int byteCount = in.readUnsignedByte();
        byte[] bytes = new byte[byteCount];
        in.readBytes(bytes);
        // convert bytes to booleans
        for (int i = 0; i < byteCount; i++) {
            byte b = bytes[i];
            for (int j = 0; j < 8; j++) {
                values[i * 8 + j] = (b & (1 << j)) != 0;
            }
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
            byte currentByte = 0;
            int bitIndex = 0;
            for (Boolean coil : values) {
                if (coil) {
                    currentByte |= (byte) (1 << bitIndex);
                }
                if (++bitIndex == 8) {
                    out.writeByte(currentByte);
                    currentByte = 0;
                    bitIndex = 0;
                }
            }
            if (bitIndex != 0) {
                out.writeByte(currentByte);
            }
            return out.array();
        } finally {
            out.release();
        }
    }

    @Override
    public String toString() {
        return "WriteMultipleCoilsRequest{" +
                "transactionId=" + getTransactionId() +
                ", unit=" + getUnitId() +
                ", functionCode=" + getFunctionCode() +
                ", startAddress=" + startAddress +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
