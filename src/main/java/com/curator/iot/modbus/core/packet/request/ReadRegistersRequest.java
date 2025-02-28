package com.curator.iot.modbus.core.packet.request;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 读取寄存器请求（03/04）
 *
 * @author Jun
 * @date 2025/2/12 
 */
public class ReadRegistersRequest extends ModbusRequest {

    private int startAddress;
    private int quantity;

    public ReadRegistersRequest() {
        super();
    }

    public ReadRegistersRequest(byte unitId, int startAddress, int quantity) {
        super(unitId, ModbusFunctionCode.READ_HOLDING_REGISTERS);
        setStartAddress(startAddress);
        setQuantity(quantity);
    }

    public ReadRegistersRequest(byte unitId, ModbusFunctionCode functionCode, int startAddress, int quantity) {
        super(unitId, functionCode);
        setStartAddress(startAddress);
        setQuantity(quantity);
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

    public void setQuantity(int quantity) {
        if (quantity < 1 || quantity > 125) {
            throw new IllegalArgumentException("Quantity must be 1-125");
        }
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.startAddress = in.readUnsignedShort();
        this.quantity = in.readUnsignedShort();
        setUnitId(unitId);
        setTransactionId(transactionId);
    }

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(5);
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeShort(startAddress);
            out.writeShort(quantity);
            return out.array();
        } finally {
            out.release();
        }
    }

    @Override
    public String toString() {
        return "ReadRegistersRequest{" +
                "transactionId=" + getTransactionId() +
                ", unit=" + getUnitId() +
                ", functionCode=" + getFunctionCode() +
                ", startAddress=" + startAddress +
                ", quantity=" + quantity +
                '}';
    }
}
