package com.example.netty.application;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

@Slf4j
public class ServerSocketHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("channelRead");
        ByteBuf buf = (ByteBuf) msg;
        String readMessage = buf.toString(Charset.forName("UTF-8"));

        log.info("response : {}", readMessage.toString());

        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("channelReadComplete");
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }
}
