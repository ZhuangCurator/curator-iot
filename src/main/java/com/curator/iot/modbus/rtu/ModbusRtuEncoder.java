package com.curator.iot.modbus.rtu;

import com.curator.iot.modbus.core.ModbusUtil;
import com.curator.iot.modbus.core.packet.ModbusPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Modbus Rtu Over TCP 编码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusRtuEncoder extends MessageToByteEncoder<ModbusPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusPacket packet, ByteBuf out) {
        int len = packet.toBytes().length + 3;
        ByteBuf buf = ctx.alloc().buffer(len);
        writePayload(buf, packet);
        writeCrc(buf);
        out.writeBytes(buf);
    }

    private void writePayload(ByteBuf buf, ModbusPacket request) {
        buf.writeByte(request.getUnitId());
        buf.writeBytes(request.toBytes());
    }

    private void writeCrc(ByteBuf buf) {
        int startReaderIndex = buf.readerIndex();
        int crc = ModbusUtil.calc16(buf);
        buf.readerIndex(startReaderIndex);
        buf.writeShort(crc);
    }
}
