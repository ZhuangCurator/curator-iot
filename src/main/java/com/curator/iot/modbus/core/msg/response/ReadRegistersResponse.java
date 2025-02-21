package com.curator.iot.modbus.core.msg.response;

import com.curator.iot.modbus.core.ModbusFunctionCode;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import io.netty.buffer.ByteBuf;

import java.util.Arrays;

/**
 * 读取保持寄存器响应
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ReadRegistersResponse extends ModbusResponse {

    private int[] registers;

    public ReadRegistersResponse() {
        super();
    }

    public ReadRegistersResponse(byte unitId, int[] registers) {
        super(unitId, ModbusFunctionCode.READ_HOLDING_REGISTERS);
        this.registers = Arrays.copyOf(registers, registers.length);
    }

    public int[] getRegisters() {
        return Arrays.copyOf(registers, registers.length);
    }

    @Override
    public void fromBytes(byte unitId, int transactionId, ByteBuf in) {
        int byteCount = in.readUnsignedByte();
        int[] registers = new int[byteCount / 2];
        for (int i = 0; i < registers.length; i++) {
            registers[i] = in.readUnsignedShort();
        }
        this.registers = Arrays.copyOf(registers, registers.length);
        setFunctionCode(ModbusFunctionCode.READ_HOLDING_REGISTERS);
        setUnitId(unitId);
        setTransactionId(transactionId);
    }
}
