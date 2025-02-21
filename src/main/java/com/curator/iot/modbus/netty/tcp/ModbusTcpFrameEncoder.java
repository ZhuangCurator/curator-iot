package com.curator.iot.modbus.netty.tcp;

import com.curator.iot.modbus.core.msg.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Modbus TCP 编码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusTcpFrameEncoder extends MessageToByteEncoder<ModbusRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusRequest request, ByteBuf out) {
        // 写入MBAP
        writeHeader(out, request);
        // 写入PDU
        writePdu(out, request);
    }

    private void writeHeader(ByteBuf out, ModbusRequest request) {
        // Transaction Identifier
        out.writeShort(request.getTransactionId());
        // 固定0x0000
        out.writeShort(0x0000);
        // 后续字节数 = Unit ID + PDU长度
        int length = 1 + request.toBytes().length;
        out.writeShort(length);
        // Unit Identifier (1 byte)
        out.writeByte(request.getUnitId());
    }

    private void writePdu(ByteBuf out, ModbusRequest request) {
        out.writeBytes(request.toBytes());
    }

}
