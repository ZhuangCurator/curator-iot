package com.curator.iot.modbus.netty.rtu;

import com.curator.iot.modbus.core.ModbusClient;
import com.curator.iot.modbus.core.ReconnectConfig;
import com.curator.iot.modbus.core.msg.ModbusRequest;
import com.curator.iot.modbus.core.msg.ModbusResponse;
import com.curator.iot.modbus.netty.tcp.ModbusTcpFrameDecoder;
import com.curator.iot.modbus.netty.tcp.ModbusTcpFrameEncoder;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.CompletableFuture;

/**
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusRtuClient extends ModbusClient {

    public ModbusRtuClient() {
        super(ReconnectConfig.defaultConfig());
    }

    public ModbusRtuClient(ReconnectConfig reconnectConfig) {
        super(reconnectConfig);
    }

    @Override
    public void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusRtuFrameEncoder())
                .addLast(new ModbusRtuFrameDecoder());
        super.initPipeline(pipeline);
    }

    @Override
    public CompletableFuture<ModbusResponse> sendRequest(ModbusRequest request) {
        CompletableFuture<ModbusResponse> future = new CompletableFuture<>();
        pendingRequests.put(0, future);

        channel.writeAndFlush(request).addListener(f -> {
            if (!f.isSuccess()) {
                pendingRequests.remove(request.getTransactionId());
                future.completeExceptionally(f.cause());
            }
        });
        return future;
    }
}
