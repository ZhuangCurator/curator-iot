package com.curator.iot.modbus.netty.tcp;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.ModbusUtil;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import com.curator.iot.modbus.core.msg.response.*;
import com.curator.iot.modbus.netty.tcp.ModbusTcpFrameDecoder.DecoderState;
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
public class ModbusTcpFrameDecoder extends ReplayingDecoder<DecoderState> {

    private int transactionId;
    private byte unitId;

    public ModbusTcpFrameDecoder() {
        super(DecoderState.READ_MBAP);
    }

    /**
     * States of the decoder.
     */
    enum DecoderState {
        /** 读取报文头 */ READ_MBAP,
        /** 读取pdu */ READ_PDU,
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        switch (state()) {
            case READ_MBAP:
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
        ModbusResponse response = parsePDU(in);
        list.add(response);
        checkpoint(DecoderState.READ_MBAP);
    }

    private ModbusResponse parsePDU(ByteBuf pdu) {
        ModbusResponse response;
        byte functionCode = pdu.readByte();
        // 处理异常响应
        if ((functionCode & 0x80) != 0) {
            byte errorCode = pdu.readByte();
            functionCode = (byte) (functionCode & 0x7F);
            response = ModbusUtil.unwrapResponse(functionCode);
            response.setTransactionId(transactionId);
            response.setUnitId(unitId);
            response.setExceptionCode(errorCode);
            response.setFunctionCode(ModbusFunctionCode.fromCode(functionCode));
        } else {
            response = ModbusUtil.unwrapResponse(functionCode);
            response.fromBytes(unitId, transactionId, pdu);
        }
        return response;
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
