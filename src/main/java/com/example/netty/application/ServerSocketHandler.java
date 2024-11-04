package com.example.netty.application;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@Slf4j
@Component
@Sharable
public class ServerSocketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        String readMessage = buf.toString(Charset.forName("UTF-8"));

        log.info("response {} {} : {}", buf.capacity(), readMessage.length(), readMessage.toString());

        ByteBuf byteBuf = Unpooled.wrappedBuffer(readMessage.getBytes());

        ChannelFuture future = ctx.writeAndFlush(byteBuf);
        buf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught", cause);
        ctx.close();
    }
}
