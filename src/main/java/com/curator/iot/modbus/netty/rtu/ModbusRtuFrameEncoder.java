package com.curator.iot.modbus.netty.rtu;

import com.curator.iot.modbus.core.ModbusUtil;
import com.curator.iot.modbus.core.msg.ModbusRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Modbus Rtu Over TCP 编码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusRtuFrameEncoder extends MessageToByteEncoder<ModbusRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ModbusRequest request, ByteBuf out) {
        int len = request.toBytes().length + 3;
        ByteBuf buf = ctx.alloc().buffer(len);
        writePayload(buf, request);
        writeCrc(buf);
        out.writeBytes(buf);
    }

    private void writePayload(ByteBuf buf, ModbusRequest request) {
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
