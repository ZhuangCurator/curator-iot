package com.curator.iot.modbus.core;

import com.curator.iot.modbus.core.slave.ModbusConfig;
import com.curator.iot.modbus.core.slave.ModbusRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Modbus Slave
 *
 * @author Jun
 * @date 2025/2/26 
 */
public class ModbusServer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final ModbusConfig config;

    public ModbusServer(ModbusConfig config) {
        this(config, new NioEventLoopGroup(), new NioEventLoopGroup());
    }

    public ModbusServer(ModbusConfig config, NioEventLoopGroup bossGroup, NioEventLoopGroup workerGroup) {
        this.config = config;
        this.bossGroup = bossGroup;
        this.workerGroup = workerGroup;
    }

    public void start() {
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            initPipeline(ch.pipeline());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(config.getPort()).sync();
            logger.info("Modbus Server [unitId: {}, port: {}] Started !", config.getSlaveId(), config.getPort());
            channelFuture.channel().closeFuture().addListener((ChannelFuture f) -> close());
        } catch (InterruptedException e) {
            logger.error("Modbus Server [unitId: {}, port: {}] start error, {}", config.getSlaveId(), config.getPort(), e.getMessage());
        }
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusRequestHandler(config));
    }

    public ModbusConfig getConfig() {
        return config;
    }

    /**
     * 关闭服务
     */
    public void close() {
        closeEventLoop(bossGroup, workerGroup);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error("Modbus Server服务关闭出现异常: {}", e.getMessage());
        }
    }

    /**
     * 关闭 EventLoopGroup
     */
    private void closeEventLoop(EventLoopGroup... group) {
        logger.info("Modbus Server [unitId: {}, port: {}] Stopped !", config.getSlaveId(), config.getPort());
        for (EventLoopGroup eventLoopGroup : group) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
