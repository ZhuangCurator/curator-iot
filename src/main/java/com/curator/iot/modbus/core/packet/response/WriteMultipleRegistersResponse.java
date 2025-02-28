package com.curator.iot.modbus.core.packet.response;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 写多个寄存器响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteMultipleRegistersResponse extends ModbusResponse {
    private int startAddress;
    private int quantity;

    public WriteMultipleRegistersResponse() {
        super();
    }

    public WriteMultipleRegistersResponse(byte unitId, int startAddress, int quantity) {
        super(unitId, ModbusFunctionCode.WRITE_MULTIPLE_REGISTERS);
        this.startAddress = startAddress;
        this.quantity = quantity;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        this.startAddress = in.readUnsignedShort();
        this.quantity = in.readUnsignedShort();
        setFunctionCode(ModbusFunctionCode.WRITE_MULTIPLE_REGISTERS);
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
}
