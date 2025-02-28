package com.curator.iot.modbus.core;

/**
 * modbus 功能码
 *
 * @author Jun
 * @date 2025/2/12
 */
public enum ModbusFunctionCode {

    /** 读取线圈（01） */ READ_COILS((byte) 0x01),
    /** 读取离散输入（02） */ READ_DISCRETE_INPUTS((byte) 0x02),
    /** 读取保持寄存器（03） */ READ_HOLDING_REGISTERS((byte) 0x03),
    /** 读取输入寄存器（04） */ READ_INPUT_REGISTERS((byte) 0x04),
    /** 写入单个线圈（05） */ WRITE_SINGLE_COIL((byte) 0x05),
    /** 写入单个保持寄存器（06） */ WRITE_SINGLE_REGISTER((byte) 0x06),
    /** 写入多个线圈（15） */ WRITE_MULTIPLE_COILS((byte) 0x0F),
    /** 写入多个保持寄存器（16） */ WRITE_MULTIPLE_REGISTERS((byte) 0x10);

    private final byte code;

    ModbusFunctionCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static ModbusFunctionCode fromCode(int code) {
        if ( code < 0 ) {
            code &= (byte) 0x7F;
        }
        for (ModbusFunctionCode functionCode : values()) {
            if (functionCode.getCode() == code) {
                return functionCode;
            }
        }
        throw new IllegalArgumentException("Unknown function code: " + code);
    }
}
