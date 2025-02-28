package com.curator.iot.modbus.rtu;

import com.curator.iot.modbus.core.DecoderState;
import com.curator.iot.modbus.core.ModbusErrorCode;
import com.curator.iot.modbus.core.ModbusUtil;
import com.curator.iot.modbus.core.packet.ModbusPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Modbus RTU OVER TCP 解码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusRtuDecoder extends ReplayingDecoder<DecoderState> {

    /** 从机解码器 */
    private final boolean isSlave;

    private byte unitId;

    public ModbusRtuDecoder(boolean isSlave) {
        super(DecoderState.READ_HEAD);
        this.isSlave = isSlave;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        switch (state()) {
            case READ_HEAD:
                readUnit(in);
                break;
            case READ_PDU:
                readPdu(in, list);
                break;
            default:
                throw new Error("Modbus Slave Rtu Decoder Shouldn't reach here.");
        }
    }

    private void readUnit(ByteBuf in) {
        unitId = in.readByte();
        checkpoint(DecoderState.READ_PDU);
    }

    private void readPdu(ByteBuf in, List<Object> list) {
        ModbusPacket packet = parsePdu(in);
        short crc = in.readShort();
        list.add(packet);
        checkpoint(DecoderState.READ_HEAD);
    }

    private ModbusPacket parsePdu(ByteBuf pdu) {
        byte functionCode = pdu.readByte();
        ModbusPacket packet;
        if(isSlave) {
            packet = ModbusUtil.unwrapRequest(functionCode);
            packet.fromBytes(unitId, 0, pdu);
        } else {
            packet = ModbusUtil.unwrapResponse(functionCode);
            if (functionCode < 0) {
                // error
                byte errorCode = pdu.readByte();
                ModbusErrorCode error = ModbusErrorCode.fromCode(errorCode);
                packet.setUnitId(unitId);
                packet.setErrorCode(error);
            } else {
                packet.fromBytes(unitId, 0, pdu);
            }
        }
        return packet;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 自定义异常处理逻辑
        if (cause instanceof CorruptedFrameException) {
            System.err.println("帧解析错误: " + cause.getMessage());
        }
        ctx.close();
    }

}
