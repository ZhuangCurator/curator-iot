package com.curator.iot.modbus.core.msg.request;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.msg.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 写单个线圈 (0x05)
 *
 * @author Jun
 * @date 2025/2/19 
 */
public class WriteSingleCoilRequest  extends ModbusRequest {

    private int address;
    private boolean value;

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

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(5);
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeShort(address);
            out.writeShort(value? 0xFF00 : 0x0000);
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
