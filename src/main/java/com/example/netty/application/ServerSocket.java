package com.example.netty.application;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ServerSocket {

    private final ServerBootstrap serverBootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Channel serverChannel;

    public ServerSocket(ServerSocketHandler serverSocketHandler) throws InterruptedException {
        log.info("ServerSocket Start");
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        try {
            this.serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(serverSocketHandler);
                    }
                });

            ChannelFuture f = serverBootstrap.bind(10035).sync();
            this.serverChannel = f.channel();
            log.info("ServerSocket bind sync");

            f.channel().closeFuture().sync();
            log.info("ServerSocket close sync");

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            log.info("Finish");
        }
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        ChannelFuture closeFuture = serverChannel.closeFuture();
        closeFuture.sync();

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}