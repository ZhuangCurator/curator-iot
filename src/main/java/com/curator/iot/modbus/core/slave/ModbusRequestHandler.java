package com.curator.iot.modbus.core.slave;

import com.curator.iot.modbus.core.ModbusException;
import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import com.curator.iot.modbus.core.packet.request.*;
import com.curator.iot.modbus.core.packet.response.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Modbus Slave 请求处理器
 *
 * @author Jun
 * @date 2025/2/26 
 */
public class ModbusRequestHandler extends SimpleChannelInboundHandler<ModbusRequest> {

    private final ModbusConfig config;
    private final ModbusDataHandler dataHandler;

    public ModbusRequestHandler(ModbusConfig config) {
        this.config = config;
        this.dataHandler = config.getDataHandler();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ModbusRequest request) {
        if (request.getUnitId() != config.getSlaveId()) {
            // 转发给其他处理器
            ctx.fireChannelRead(request);
            return;
        }
        ModbusResponse response = processRequest(request);
        ctx.writeAndFlush(response);
    }

    private ModbusResponse processRequest(ModbusRequest request) {
        try {
            switch (request.getFunctionCode()) {
                case READ_COILS:
                    return handleReadCoils((ReadCoilsRequest) request);
                case READ_DISCRETE_INPUTS:
                    return handleReadDiscreteInputs((ReadCoilsRequest) request);
                case READ_HOLDING_REGISTERS:
                    return handleReadHoldingRegisters((ReadRegistersRequest) request);
                case READ_INPUT_REGISTERS:
                    return handleReadInputRegisters((ReadRegistersRequest) request);
                case WRITE_SINGLE_COIL:
                    return handleWriteCoil((WriteSingleCoilRequest) request);
                case WRITE_SINGLE_REGISTER:
                    return handleWriteHoldingRegister((WriteSingleRegisterRequest) request);
                case WRITE_MULTIPLE_COILS:
                    return handleWriteCoils((WriteMultipleCoilsRequest) request);
                case WRITE_MULTIPLE_REGISTERS:
                    return handleWriteHoldingRegisters((WriteMultipleRegistersRequest) request);
                // 其他功能码处理...
                default:
                    return handleException(request);
            }
        } catch (ModbusException e) {
            return handleException(request);
        }
    }

    private ModbusResponse handleReadCoils(ReadCoilsRequest request) {
        int startAddress = request.getStartAddress();
        int quantity = request.getQuantity();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + quantity - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }

        byte[] values = dataHandler.readCoils(startAddress, quantity);
        ReadCoilsResponse response = new ReadCoilsResponse(request.getUnitId(), values);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleReadDiscreteInputs(ReadCoilsRequest request) {
        int startAddress = request.getStartAddress();
        int quantity = request.getQuantity();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + quantity - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }

        byte[] values = dataHandler.readDiscreteInputs(startAddress, quantity);
        ReadCoilsResponse response = new ReadCoilsResponse(request.getUnitId(), values);
        response.setFunctionCode(ModbusFunctionCode.READ_DISCRETE_INPUTS);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleReadHoldingRegisters(ReadRegistersRequest request) {
        int startAddress = request.getStartAddress();
        int quantity = request.getQuantity();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + quantity - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }

        int[] values = dataHandler.readHoldingRegisters(startAddress, quantity);
        ReadRegistersResponse response = new ReadRegistersResponse(request.getUnitId(), values);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleReadInputRegisters(ReadRegistersRequest request) {
        int startAddress = request.getStartAddress();
        int quantity = request.getQuantity();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + quantity - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }

        int[] values = dataHandler.readInputRegisters(startAddress, quantity);
        ReadRegistersResponse response = new ReadRegistersResponse(request.getUnitId(), values);
        response.setTransactionId(request.getTransactionId());
        response.setFunctionCode(ModbusFunctionCode.READ_INPUT_REGISTERS);
        return response;
    }

    private ModbusResponse handleWriteCoil(WriteSingleCoilRequest request) {
        int address = request.getAddress();
        boolean value = request.getValue();

        if (!dataHandler.isValidAddress(address)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }
        dataHandler.writeCoil(address, value);

        WriteSingleCoilResponse response = new WriteSingleCoilResponse(request.getUnitId(), address, value);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleWriteHoldingRegister(WriteSingleRegisterRequest request) {
        int address = request.getAddress();
        int value = request.getValue();

        if (!dataHandler.isValidAddress(address)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }
        dataHandler.writeHoldingRegister(address, value);

        WriteSingleRegisterResponse response = new WriteSingleRegisterResponse(request.getUnitId(), address, value);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleWriteCoils(WriteMultipleCoilsRequest request) {
        int startAddress = request.getStartAddress();
        boolean[] values = request.getValues();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + values.length - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }
        dataHandler.writeCoils(startAddress, values);

        WriteMultipleCoilsResponse response = new WriteMultipleCoilsResponse(request.getUnitId(), startAddress, values.length);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleWriteHoldingRegisters(WriteMultipleRegistersRequest request) {
        int startAddress = request.getStartAddress();
        int[] values = request.getValues();

        if (!dataHandler.isValidAddress(startAddress) ||
                !dataHandler.isValidAddress(startAddress + values.length - 1)) {
//            throw new ModbusException(ModbusExceptionCode.ILLEGAL_DATA_ADDRESS);
            return handleException(request);
        }
        dataHandler.writeHoldingRegisters(startAddress, values);

        WriteMultipleRegistersResponse response = new WriteMultipleRegistersResponse(request.getUnitId(), startAddress, values.length);
        response.setTransactionId(request.getTransactionId());
        return response;
    }

    private ModbusResponse handleException(ModbusRequest request) throws ModbusException {
        return null;
    }
}
