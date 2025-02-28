package com.curator.iot.modbus.core.slave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jun
 * @date 2025/2/26 
 */
public class DefaultDataHandler implements ModbusDataHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultDataHandler() {
        init();
    }

    private final Map<Integer, Short> holdingRegisters = new ConcurrentHashMap<>();
    private final Map<Integer, Short> inputRegisters = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> coils = new ConcurrentHashMap<>();
    private final Map<Integer, Boolean> discreteInputs = new ConcurrentHashMap<>();

    @Override
    public byte[] readCoils(int startAddress, int quantity) {
        boolean[] result = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = coils.getOrDefault(startAddress + i, false);
        }
        logger.info("Read Coils: {}", result);

        return boolToByte(result);
    }

    @Override
    public byte[] readDiscreteInputs(int startAddress, int quantity) {
        boolean[] result = new boolean[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = discreteInputs.getOrDefault(startAddress + i, false);
        }
        logger.info("Read DiscreteInputs: {}", result);
        return boolToByte(result);
    }

    @Override
    public int[] readHoldingRegisters(int startAddress, int quantity) {
        int[] result = new int[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = holdingRegisters.getOrDefault(startAddress + i, (short) 0);
        }
        logger.info("Read HoldingRegisters: {}", result);
        return result;
    }

    @Override
    public int[] readInputRegisters(int startAddress, int quantity) {
        int[] result = new int[quantity];
        for (int i = 0; i < quantity; i++) {
            result[i] = inputRegisters.getOrDefault(startAddress + i, (short) 0);
        }
        logger.info("Read InputRegisters: {}", result);
        return result;
    }

    @Override
    public void writeCoil(int address, boolean value) {
        coils.put(address, value);
        logger.info("Write coil [address: {}, val: {}]", address, value);
    }

    @Override
    public void writeHoldingRegister(int address, int value) {
        holdingRegisters.put(address, (short) value);
        logger.info("Write holding register [address: {}, val: {}]", address, value);
    }

    public void init() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            holdingRegisters.put(i, (short) random.nextInt(100));
            inputRegisters.put(i, (short) random.nextInt(100));
            coils.put(i, random.nextInt(10) < 6);
            discreteInputs.put(i, random.nextInt(10) < 6);
        }
        logger.info("DefaultDataHandler Init data!");
    }
}
