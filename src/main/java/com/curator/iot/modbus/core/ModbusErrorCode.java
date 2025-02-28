package com.curator.iot.modbus.core;

/**
 * @author Jun
 * @date 2025/2/28 
 */
public enum ModbusErrorCode {

    /** 非法功能码（01） */ ILLEGAL_FUNCTION((byte) 0x01),
    /** 非法数据地址（02） */ ILLEGAL_DATA_ADDRESS((byte) 0x02),
    /** 非法数据值（03） */ ILLEGAL_DATA_VALUE((byte) 0x03),
    /** 服务器故障（04） */ SERVER_DEVICE_FAILURE((byte) 0x04),
    /** 企鹅人（05） */ ACKNOWLEDGE((byte) 0x05),
    /** 服务器繁忙（06） */ SERVER_DEVICE_BUSY((byte) 0x06),
    /** 写入多个线圈（15） */ WRITE_MULTIPLE_COILS((byte) 0x0F),
    /** 写入多个保持寄存器（16） */ WRITE_MULTIPLE_REGISTERS((byte) 0x10);

    private final byte code;

    ModbusErrorCode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static ModbusErrorCode fromCode(int code) {
        for (ModbusErrorCode functionCode : values()) {
            if (functionCode.getCode() == code) {
                return functionCode;
            }
        }
        throw new IllegalArgumentException("Unknown function code: " + code);
    }
}
