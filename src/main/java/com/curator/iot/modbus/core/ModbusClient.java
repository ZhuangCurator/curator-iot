package com.curator.iot.modbus.core;

import com.curator.iot.modbus.core.msg.ModbusResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Jun
 * @date 2025/2/12 
 */
public abstract class ModbusClient extends ModbusFunction {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static final long DEFAULT_REPLY_TIMEOUT = TimeUnit.SECONDS.toMillis(1);
    protected final ConcurrentMap<Integer, CompletableFuture<ModbusResponse>> pendingRequests = new ConcurrentHashMap<>();
    protected final Bootstrap bootstrap;
    protected final ReconnectConfig reconnectConfig;
    protected volatile Channel channel;
    protected volatile boolean manualClose = false;
    protected int reconnectAttempts = 0;
    protected ScheduledFuture<?> reconnectFuture;

    public ModbusClient(ReconnectConfig reconnectConfig) {
        this.reconnectConfig = reconnectConfig;
        this.bootstrap = new Bootstrap()
                .group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        initPipeline(ch.pipeline());
                    }
                });
        setReplyTimeout(DEFAULT_REPLY_TIMEOUT);
    }

    protected void initPipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new ModbusChannelHandler());
    }

    public CompletableFuture<Void> connect(String host, int port) {
        manualClose = false;
        return doConnect(host, port, 0);
    }

    private CompletableFuture<Void> doConnect(String host, int port, long delayMs) {
        CompletableFuture<Void> connectFuture = new CompletableFuture<>();

        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener((ChannelFuture f) -> {
            if (f.isSuccess()) {
                this.channel = f.channel();
                reconnectAttempts = 0;
                logger.info("Connected to {}:{}", host, port);
                connectFuture.complete(null);
            } else {
                connectFuture.completeExceptionally(f.cause());
            }
        });

        // 添加延迟连接逻辑
        if (delayMs > 0) {
            channelFuture.addListener(f -> {
                if (!f.isSuccess() && needReconnect()) {
                    scheduleReconnect(host, port);
                }
            });
        }

        return connectFuture;
    }


    private final class ModbusChannelHandler extends SimpleChannelInboundHandler<ModbusResponse> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ModbusResponse response) throws Exception {
            CompletableFuture<ModbusResponse> resFuture = pendingRequests.remove(response.getTransactionId());
            if (resFuture != null) {
                resFuture.complete(response);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (needReconnect()) {
                scheduleReconnect(
                        ((InetSocketAddress) ctx.channel().remoteAddress()).getHostName(),
                        ((InetSocketAddress) ctx.channel().remoteAddress()).getPort()
                );
            }
        }

    }

    /**
     * 判断是否重连
     */
    private boolean needReconnect() {
        return reconnectConfig.isAutoReconnect() &&
                !manualClose &&
                (reconnectConfig.getMaxRetries() <= 0 ||
                        reconnectAttempts < reconnectConfig.getMaxRetries());
    }

    /**
     * 定时重连
     */
    private void scheduleReconnect(String host, int port) {
        if (reconnectFuture != null && !reconnectFuture.isDone()) {
            return;
        }
        // 重连间隔 指数退避算法
        long delay = calculateReconnectDelay();
        reconnectAttempts++;
        reconnectFuture = bootstrap.config().group().schedule(() -> {
            doConnect(host, port, delay).whenComplete((v, e) -> {
                if (e != null && needReconnect()) {
                    scheduleReconnect(host, port);
                }
            });
        }, delay, TimeUnit.MILLISECONDS);
    }

    private long calculateReconnectDelay() {
        long delay = (long) (reconnectConfig.getInitialInterval() *
                Math.pow(reconnectConfig.getMultiplier(), reconnectAttempts));
        return Math.min(delay, reconnectConfig.getMaxInterval());
    }

    public void close() {
        manualClose = true;
        if (channel != null) {
            channel.close();
        }
        if (reconnectFuture != null) {
            reconnectFuture.cancel(false);
        }
        bootstrap.config().group().shutdownGracefully();
    }

}
