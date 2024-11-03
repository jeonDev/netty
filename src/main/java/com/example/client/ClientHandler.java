package com.example.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("channelActive");

        IntStream.range(0, 10).forEach(taskId -> {
            log.info("taskId : {}",taskId);
            String sendMessage = "Hello Netty";

            ByteBuf buf = Unpooled.buffer();
            buf.writeBytes(sendMessage.getBytes(StandardCharsets.UTF_8));

            log.info("request : {}", sendMessage);

            ctx.writeAndFlush(buf);
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("channelRead");
        ByteBuf buf = (ByteBuf) msg;
        String readMessage = buf.toString(Charset.defaultCharset());

        log.info("response : {}", readMessage);
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        log.info("channelReadComplete");
//        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("exceptionCaught");

        cause.printStackTrace();
        ctx.close();
    }
}
