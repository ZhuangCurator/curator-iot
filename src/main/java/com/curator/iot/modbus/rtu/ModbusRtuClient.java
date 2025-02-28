package com.curator.iot.modbus.rtu;

import com.curator.iot.modbus.core.ModbusClient;
import com.curator.iot.modbus.core.master.ReconnectConfig;
import com.curator.iot.modbus.core.packet.ModbusRequest;
import com.curator.iot.modbus.core.packet.ModbusResponse;
import com.curator.iot.modbus.master.netty.rtu.ModbusRtuClientDecoder;
import com.curator.iot.modbus.master.netty.rtu.ModbusRtuClientEncoder;
import io.netty.channel.ChannelPipeline;

import java.util.concurrent.CompletableFuture;

/**
 * Modbus Rtu Master
 *
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
        pipeline.addLast(new ModbusRtuEncoder())
                .addLast(new ModbusRtuDecoder(false));
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
