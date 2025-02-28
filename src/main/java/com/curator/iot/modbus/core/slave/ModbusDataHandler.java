package com.curator.iot.modbus.core.slave;

/**
 * 数据处理器接口
 *
 * @author Jun
 * @date 2025/2/26 
 */
public interface ModbusDataHandler {

    /**
     * 有效地址校验
     *
     * @param address 地址
     * @return boolean
     */
    default boolean isValidAddress(int address) {
        return address >= 0 && address < 0xFFFF;
    }

    /**
     * bool数组到字节数组
     *
     * @param values 数值
     * @return {@link byte[] }
     */
    default byte[] boolToByte(boolean[] values) {
        byte[] result = new byte[values.length / 8 + 1];
        for (int i = 0; i < values.length; i++) {
            int byteIndex = i / 8;
            int bitIndex = i % 8;
            if (values[i]) {
                result[byteIndex] |= (byte) (1 << bitIndex);
            }
        }
        return result;
    }

    /**
     * 读取线圈 0x01
     *
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link byte[] }
     */
    byte[] readCoils(int startAddress, int quantity);

    /**
     * 读取离散输入 0x02
     *
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link boolean[] }
     */
    byte[] readDiscreteInputs(int startAddress, int quantity);


    /**
     * 读取保持寄存器 0x03
     *
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link int[] }
     */
    int[] readHoldingRegisters(int startAddress, int quantity);

    /**
     * 读取输入寄存器 0x04
     *
     * @param startAddress 起始地址
     * @param quantity 数量
     * @return {@link int[] }
     */
    int[] readInputRegisters(int startAddress, int quantity);

    /**
     * 写入线圈 0x05
     *
     * @param address 地址
     * @param value 值
     */
    void writeCoil(int address, boolean value);

    /**
     * 写保持寄存器 0x06
     *
     * @param address 地址
     * @param value 值
     */
    void writeHoldingRegister(int address, int value);

    /**
     * 写多个线圈 0x0F
     *
     * @param startAddress 起始地址
     * @param values 数值
     */
    default void writeCoils(int startAddress, boolean[] values) {
        for (int i = 0; i < values.length; i++) {
            writeCoil(startAddress + i, values[i]);
        }
    }

    /**
     * 写多个保持寄存器 0x10
     *
     * @param startAddress 起始地址
     * @param values 数值
     */
    default void writeHoldingRegisters(int startAddress, int[] values) {
        for (int i = 0; i < values.length; i++) {
            writeHoldingRegister(startAddress + i, values[i]);
        }
    }
}
