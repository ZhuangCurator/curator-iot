package com.curator.iot.modbus.core.msg.request;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.msg.ModbusRequest;
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

    public void setValues(boolean[] values) {
        if (values == null || values.length == 0 || values.length > 0x7B0) {
            throw new IllegalArgumentException("Invalid values array");
        }
        this.values = values.clone();
    }

    public int getQuantity() { return values.length; }
    public int getByteCount() { return (int) Math.ceil(values.length / 8.0); }

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
