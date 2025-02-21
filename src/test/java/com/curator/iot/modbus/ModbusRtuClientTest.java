package com.curator.iot.modbus;

import com.curator.iot.modbus.core.ModbusClient;
import com.curator.iot.modbus.core.msg.response.*;
import com.curator.iot.modbus.netty.rtu.ModbusRtuClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jun
 * @date 2025/2/20 
 */
public class ModbusRtuClientTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    ModbusClient client;

    @Before
    public void setUp() {
        client = new ModbusRtuClient();
        try {
            client.connect("127.0.0.1", 502).get();
        } catch (Exception e) {
            logger.error("ModbusRtuClient Connect Error: {}", e.getMessage());
        }
    }

    @Test
    public void readCoils() {
        try {
            logger.error("readCoils start");
            client.setReplyTimeout(50000);
            ReadCoilsResponse response = client.readCoils(1, 0, 10);
            for (int i = 0; i < 10; i++) {
                logger.error("coil: {}, val: {}", i + 1, response.getCoilStatus(i));
            }
        } catch (Exception e) {
            logger.error("readCoils error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void readDiscreteInputs() {
        try {
            logger.error("readDiscreteInputs start");
            ReadCoilsResponse response = client.readDiscreteInputs(1, 0, 10);
            for (int i = 0; i < 10; i++) {
                logger.error("discreteInput: {}, val: {}", i + 1, response.getCoilStatus(i));
            }
        } catch (Exception e) {
            logger.error("readDiscreteInputs error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void readHoldingRegisters() {
        try {
            logger.error("readRegisters start");
            ReadRegistersResponse response = client.readHoldingRegisters(1, 0, 3);
            if (response.isException()) {
                logger.error("readRegisters ex: {}", response.getExceptionCode());
            } else {
                for (int i = 0; i < 3; i++) {
                    logger.error("holdingRegister: {}, val: {}", i + 1, response.getRegisters()[i]);
                }
            }
        } catch (Exception e) {
            logger.error("readRegisters error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void readInputRegisters() {
        try {
            client.setReplyTimeout(5000);
            logger.error("readInputRegisters start");
            ReadRegistersResponse response = client.readInputRegisters(1, 0, 3);
            for (int i = 0; i < 3; i++) {
                logger.error("inputRegister: {}, val: {}", i + 1, response.getRegisters()[i]);
            }
        } catch (Exception e) {
            logger.error("readInputRegisters error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void writeSingleCoil() {
        try {
            logger.error("writeSingleCoil start");
            WriteSingleCoilResponse response = client.writeSingleCoil(1, 2, true);
            logger.error("writeSingleCoil: {}, val: {}", response.getAddress(), response.getValue());

        } catch (Exception e) {
            logger.error("writeSingleCoil error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void writeMultipleCoils() {
        try {
            logger.error("writeMultipleCoils start");
            boolean[] values = {true, false, true, false, true, true, false, false, true};
            WriteMultipleCoilsResponse response = client.writeMultipleCoils(1, 0, values);
            logger.error("writeMultipleCoils: {}, quantity: {}", response.getStartAddress(), response.getQuantity());
        } catch (Exception e) {
            logger.error("writeMultipleCoils error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void writeSingleRegister() {
        try {
            logger.error("writeSingleRegister start");
            WriteSingleRegisterResponse response = client.writeSingleRegister(1, 1, 22);
            logger.error("writeSingleRegister: {}, val: {}", response.getAddress(), response.getValue());
        } catch (Exception e) {
            logger.error("writeSingleRegister error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }

    @Test
    public void writeMultipleRegisters() {
        try {
            logger.error("writeMultipleRegisters start");
            int[] values = {22, 33, 44, 55, 66, 77, 88, 99};
            WriteMultipleRegistersResponse response = client.writeMultipleRegisters(1, 1, values);
            logger.error("writeMultipleRegisters: {}, quantity: {}", response.getStartAddress(), response.getQuantity());
        } catch (Exception e) {
            logger.error("writeMultipleRegisters error: {}", e.getMessage());
        } finally {
            client.close();
        }
    }
}
