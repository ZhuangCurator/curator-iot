package com.curator.iot.modbus.rtu;

import com.curator.iot.modbus.core.slave.ModbusConfig;
import com.curator.iot.modbus.core.ModbusServer;
import com.curator.iot.modbus.slave.netty.rtu.ModbusRtuServerDecoder;
import com.curator.iot.modbus.slave.netty.rtu.ModbusRtuServerEncoder;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Modbus Rtu Slave
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusRtuServer extends ModbusServer {

    public ModbusRtuServer() {
        this(ModbusConfig.defaultConfig());
    }

    public ModbusRtuServer(ModbusConfig modbusConfig) {
        this(modbusConfig, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    public ModbusRtuServer(ModbusConfig config, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        super(config, bossGroup, workerGroup);
    }

    @Override
    public void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusRtuEncoder())
                .addLast(new ModbusRtuDecoder(true));
        super.initPipeline(pipeline);
    }

}
