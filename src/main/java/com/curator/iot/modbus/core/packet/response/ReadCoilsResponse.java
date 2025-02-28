package com.curator.iot.modbus.core.packet.response;


import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;

/**
 * 读取线圈状态响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ReadCoilsResponse extends ModbusResponse {

    private byte[] coilStatus;

    public ReadCoilsResponse() {
        super();
    }

    public ReadCoilsResponse(byte unitId, byte[] coilStatus) {
        super(unitId, ModbusFunctionCode.READ_COILS);
        this.coilStatus = Arrays.copyOf(coilStatus, coilStatus.length);
    }

    public boolean getCoilStatus(int coilIndex) {
        int byteIndex = coilIndex / 8;
        int bitIndex = coilIndex % 8;
        return (coilStatus[byteIndex] & (1 << bitIndex)) != 0;
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        int byteCount = in.readUnsignedByte();
        byte[] coilStatus = new byte[byteCount];
        in.readBytes(coilStatus);
        this.coilStatus = Arrays.copyOf(coilStatus, coilStatus.length);
        setFunctionCode(ModbusFunctionCode.READ_COILS);
        setUnitId(unitId);
        setTransactionId(transactionId);
    }

    @Override
    public byte[] toBytes() {
        ByteBuf out = Unpooled.buffer(2 + coilStatus.length);
        try {
            out.writeByte(getFunctionCode().getCode());
            out.writeByte(coilStatus.length);
            out.writeBytes(coilStatus);
            return out.array();
        } finally {
            out.release();
        }
    }
}
