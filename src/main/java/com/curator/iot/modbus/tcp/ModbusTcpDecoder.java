package com.curator.iot.modbus.tcp;

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
 * Modbus TCP 解码器
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusTcpDecoder extends ReplayingDecoder<DecoderState> {

    /** 从机解码器 */
    private final boolean isSlave;

    private int transactionId;
    private byte unitId;

    public ModbusTcpDecoder(boolean isSlave) {
        super(DecoderState.READ_HEAD);
        this.isSlave = isSlave;
    }


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        switch (state()) {
            case READ_HEAD:
                readMbap(in);
                break;
            case READ_PDU:
                readPdu(in, list);
                break;
            default:
                throw new Error("Shouldn't reach here.");
        }
    }

    private void readMbap(ByteBuf in) {
        transactionId = in.readUnsignedShort();
        // 协议标识及数据长度
        in.skipBytes(4);
        unitId = in.readByte();
        checkpoint(DecoderState.READ_PDU);
    }

    private void readPdu(ByteBuf in, List<Object> list) {
        ModbusPacket packet = parsePdu(in);
        list.add(packet);
        checkpoint(DecoderState.READ_HEAD);
    }

    private ModbusPacket parsePdu(ByteBuf pdu) {
        byte functionCode = pdu.readByte();
        ModbusPacket packet;
        if(isSlave) {
            packet = ModbusUtil.unwrapRequest(functionCode);
            packet.fromBytes(unitId, transactionId, pdu);
        } else {
            packet = ModbusUtil.unwrapResponse(functionCode);
            if (functionCode < 0) {
                // error
                byte errorCode = pdu.readByte();
                ModbusErrorCode error = ModbusErrorCode.fromCode(errorCode);
                packet.setTransactionId(transactionId);
                packet.setUnitId(unitId);
                packet.setErrorCode(error);
            } else {
                packet.fromBytes(unitId, transactionId, pdu);
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
