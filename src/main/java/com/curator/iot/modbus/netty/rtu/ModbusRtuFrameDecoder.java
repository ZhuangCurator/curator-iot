package com.curator.iot.modbus.netty.rtu;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.ModbusUtil;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import com.curator.iot.modbus.core.msg.response.*;
import com.curator.iot.modbus.netty.rtu.ModbusRtuFrameDecoder.DecoderState;
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
public class ModbusRtuFrameDecoder extends ReplayingDecoder<DecoderState> {

    private byte unitId;

    public ModbusRtuFrameDecoder() {
        super(DecoderState.READ_UNIT);
    }

    /**
     * States of the decoder.
     */
    enum DecoderState {
        /** 读取报文头 */ READ_UNIT,
        /** 读取pdu */ READ_PDU,
        /** Error */ ERROR,
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) throws Exception {
        switch (state()) {
            case READ_UNIT:
                readUnit(in);
                break;
            case READ_PDU:
                readPdu(in, list);
                break;
            case ERROR:
                in.skipBytes(in.readableBytes());
                checkpoint(DecoderState.READ_UNIT);
                break;
            default:
                throw new Error("Shouldn't reach here.");
        }
    }

    private void readUnit(ByteBuf in) {
        int startIndex = in.readerIndex();
        int messageLength = getMessageLength(in, startIndex);
        if (in.readableBytes() < messageLength) {
            return;
        }
        if (messageLength < 1) {
            checkpoint(DecoderState.ERROR);
            return;
        }
        unitId = in.readByte();
        checkpoint(DecoderState.READ_PDU);
    }

    private void readPdu(ByteBuf in, List<Object> list) {
        ModbusResponse response = parsePDU(in);
        short crc = in.readShort();
        list.add(response);
        checkpoint(DecoderState.READ_UNIT);
    }

    private ModbusResponse parsePDU(ByteBuf pdu) {
        ModbusResponse response;
        byte functionCode = pdu.readByte();
        // 处理异常响应
        if ((functionCode & 0x80) != 0) {
            byte errorCode = pdu.readByte();
            functionCode = (byte) (functionCode & 0x7F);
            response = ModbusUtil.unwrapResponse(functionCode);
            response.setUnitId(unitId);
            response.setExceptionCode(errorCode);
            response.setFunctionCode(ModbusFunctionCode.fromCode(functionCode));
        } else {
            response = ModbusUtil.unwrapResponse(functionCode);
            response.fromBytes(unitId, 0, pdu);
        }
        return response;
    }

    private int getMessageLength(ByteBuf byteBuf, int startIndex) {
        int funCode = byteBuf.getUnsignedByte(startIndex + 1);
        if (funCode >= 0x80) {
            return 5;
        }
        switch (funCode) {
            case 1:
            case 2:
            case 3:
            case 4:
                int length = byteBuf.getUnsignedByte(startIndex + 2);
                // 3: 1个字节的从机地址 + 1个字节的功能码 +  + 1个字节的长度字段 + 2个字节的CRC校验码
                return (3 + length + 2);
            case 5:
            case 6:
            case 15:
            case 16:
                return 8;
            default:
                return 0;
        }
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
