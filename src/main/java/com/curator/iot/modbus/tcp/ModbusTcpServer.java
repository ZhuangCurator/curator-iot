package com.curator.iot.modbus.tcp;

import com.curator.iot.modbus.core.slave.ModbusConfig;
import com.curator.iot.modbus.core.ModbusServer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Modbus Tcp Slave
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusTcpServer extends ModbusServer {

    public ModbusTcpServer() {
        this(ModbusConfig.defaultConfig());
    }

    public ModbusTcpServer(ModbusConfig modbusConfig) {
        this(modbusConfig, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    public ModbusTcpServer(ModbusConfig config, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        super(config, bossGroup, workerGroup);
    }

    @Override
    public void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusTcpEncoder())
                .addLast(new ModbusTcpDecoder(true));
        super.initPipeline(pipeline);
    }

}
