package com.curator.iot.modbus.tcp;

import com.curator.iot.modbus.core.packet.ModbusPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Modbus TCP 编码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusTcpEncoder extends MessageToByteEncoder<ModbusPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusPacket packet, ByteBuf out) {
        // 写入MBAP
        writeHeader(out, packet);
        // 写入PDU
        writePdu(out, packet);
    }

    private void writeHeader(ByteBuf out, ModbusPacket packet) {
        // Transaction Identifier
        out.writeShort(packet.getTransactionId());
        // 固定0x0000
        out.writeShort(0x0000);
        // 后续字节数 = Unit ID + PDU长度
        int length = 1 + packet.toBytes().length;
        out.writeShort(length);
        // Unit Identifier (1 byte)
        out.writeByte(packet.getUnitId());
    }

    private void writePdu(ByteBuf out, ModbusPacket packet) {
        out.writeBytes(packet.toBytes());
    }

}
