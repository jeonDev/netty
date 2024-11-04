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

    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;
    private final Channel serverChannel;

    public ServerSocket(ServerSocketHandler serverSocketHandler) throws InterruptedException {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 128)
            .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(256, 512, 2048))
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(serverSocketHandler);
                }
            });

        ChannelFuture f = serverBootstrap.bind(10035).sync();
        this.serverChannel = f.channel();

        f.channel().closeFuture().sync();
    }

    @PreDestroy
    public void destroy() throws InterruptedException {
        ChannelFuture closeFuture = serverChannel.closeFuture();
        closeFuture.sync();

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

}