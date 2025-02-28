package com.curator.iot.modbus.tcp;

import com.curator.iot.modbus.core.ModbusClient;
import com.curator.iot.modbus.core.master.ReconnectConfig;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.CompletableFuture;

/**
 * Modbus Tcp Master
 *
 * @author Jun
 * @date 2025/2/17 
 */
public class ModbusTcpClient extends ModbusClient {

    public ModbusTcpClient() {
        super(ReconnectConfig.defaultConfig());
    }

    public ModbusTcpClient(ReconnectConfig reconnectConfig) {
        super(reconnectConfig);
    }

    @Override
    public void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusTcpEncoder())
                .addLast(new ModbusTcpDecoder(false));
        super.initPipeline(pipeline);
    }

    @Override
    public CompletableFuture<ModbusResponse> sendRequest(ModbusRequest request) {
        CompletableFuture<ModbusResponse> future = new CompletableFuture<>();
        pendingRequests.put(request.getTransactionId(), future);

        channel.writeAndFlush(request).addListener(f -> {
            if (!f.isSuccess()) {
                pendingRequests.remove(request.getTransactionId());
                future.completeExceptionally(f.cause());
            }
        });
        return future;
    }
}
