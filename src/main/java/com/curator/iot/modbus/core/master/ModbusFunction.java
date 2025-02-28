package com.curator.iot.modbus.core.master;

import com.curator.iot.modbus.core.ModbusException;
import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import com.curator.iot.modbus.core.packet.request.*;
import com.curator.iot.modbus.core.packet.response.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * modbus master 功能
 *
 * @author Jun
 * @date 2025/2/19 
 */
public abstract class ModbusFunction {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected long replyTimeout;

    public void setReplyTimeout(long replyTimeout) {
        this.replyTimeout = replyTimeout;
    }

    /**
     * 发送请求
     *
     * @param request 请求
     * @return {@link CompletableFuture }<{@link ModbusResponse }>
     */
    public abstract CompletableFuture<ModbusResponse> sendRequest(ModbusRequest request);

    public ModbusResponse sendSync(ModbusRequest request) {
        CompletableFuture<ModbusResponse> future = sendRequest(request);
        try {
            return future.get(replyTimeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            logger.warn("Timeout waiting for response to {}", request);
            throw new ModbusException(String.format("Timeout waiting for response to %s.", request), e);
        } catch (InterruptedException e) {
            logger.warn("Interrupted waiting for response to {}", request);
            throw new ModbusException(String.format("Interrupted waiting for response to %s.", request), e);
        } catch (Exception e) {
            logger.warn("Internal exception waiting for response to {}", request);
            throw new ModbusException(String.format("Internal exception waiting for response to %s", request), e);
        }
    }

    /**
     * 读取线圈
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link ModbusResponse }
     */
    public ReadCoilsResponse readCoils(int unitId, int startAddress, int quantity) {
        ReadCoilsRequest readCoilsRequest = new ReadCoilsRequest((byte) unitId, startAddress, quantity);
        return (ReadCoilsResponse) sendSync(readCoilsRequest);
    }

    /**
     * 读取离散输入
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link ModbusResponse }
     */
    public ReadCoilsResponse readDiscreteInputs(int unitId, int startAddress, int quantity) {
        ReadCoilsRequest readCoilsRequest = new ReadCoilsRequest((byte) unitId, ModbusFunctionCode.READ_DISCRETE_INPUTS, startAddress, quantity);
        return (ReadCoilsResponse) sendSync(readCoilsRequest);
    }

    /**
     * 读保持寄存器
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link ModbusResponse }
     */
    public ReadRegistersResponse readHoldingRegisters(int unitId, int startAddress, int quantity) {
        ReadRegistersRequest readRegistersRequest = new ReadRegistersRequest((byte) unitId, startAddress, quantity);
        return (ReadRegistersResponse) sendSync(readRegistersRequest);
    }

    /**
     * 读输入寄存器
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link ModbusResponse }
     */
    public ReadRegistersResponse readInputRegisters(int unitId, int startAddress, int quantity) {
        ReadRegistersRequest readRegistersRequest = new ReadRegistersRequest((byte) unitId, ModbusFunctionCode.READ_INPUT_REGISTERS, startAddress, quantity);
        return (ReadRegistersResponse) sendSync(readRegistersRequest);
    }

    /**
     * 写多个线圈
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param values 数值
     * @return {@link ModbusResponse }
     */
    public WriteMultipleCoilsResponse writeMultipleCoils(int unitId, int startAddress, boolean[] values) {
        WriteMultipleCoilsRequest writeMultipleCoilsRequest = new WriteMultipleCoilsRequest((byte) unitId, startAddress, values);
        return (WriteMultipleCoilsResponse) sendSync(writeMultipleCoilsRequest);
    }

    /**
     * 写多个保持寄存器
     *
     * @param unitId 从机ID
     * @param startAddress 起始地址
     * @param values 数值
     * @return {@link ModbusResponse }
     */
    public WriteMultipleRegistersResponse writeMultipleRegisters(int unitId, int startAddress, int[] values) {
        WriteMultipleRegistersRequest writeMultipleRegistersRequest = new WriteMultipleRegistersRequest((byte) unitId, startAddress, values);
        return (WriteMultipleRegistersResponse) sendSync(writeMultipleRegistersRequest);
    }


    /**
     * 写单线圈
     *
     * @param unitId 从机ID
     * @param address 地址
     * @param value 值
     * @return {@link ModbusResponse }
     */
    public WriteSingleCoilResponse writeSingleCoil(int unitId, int address, boolean value) {
        WriteSingleCoilRequest writeSingleCoilRequest = new WriteSingleCoilRequest((byte) unitId, address, value);
        return (WriteSingleCoilResponse) sendSync(writeSingleCoilRequest);
    }


    /**
     * 写单个保持寄存器
     *
     * @param unitId 从机ID
     * @param address 地址
     * @param value 值
     * @return {@link ModbusResponse }
     */
    public WriteSingleRegisterResponse writeSingleRegister(int unitId, int address, int value) {
        WriteSingleRegisterRequest writeSingleRegisterRequest = new WriteSingleRegisterRequest((byte) unitId, address, value);
        return (WriteSingleRegisterResponse) sendSync(writeSingleRegisterRequest);
    }
}
