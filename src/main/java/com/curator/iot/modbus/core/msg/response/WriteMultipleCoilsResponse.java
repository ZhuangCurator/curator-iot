package com.curator.iot.modbus.core.msg.response;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import io.netty.buffer.ByteBuf;

/**
 * 写多个线圈响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class WriteMultipleCoilsResponse extends ModbusResponse {

    private int startAddress;
    private int quantity;

    public WriteMultipleCoilsResponse() {
        super();
    }

    public WriteMultipleCoilsResponse(byte unitId, int startAddress, int quantity) {
        super(unitId, ModbusFunctionCode.WRITE_MULTIPLE_COILS);
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
        setFunctionCode(ModbusFunctionCode.WRITE_MULTIPLE_COILS);
        setUnitId(unitId);
        setTransactionId(transactionId);
    }
}
