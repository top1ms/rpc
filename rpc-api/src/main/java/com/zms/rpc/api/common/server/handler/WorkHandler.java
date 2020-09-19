package com.zms.rpc.api.common.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class WorkHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Bad boy Wcl");
        ByteBuf result = (ByteBuf) msg;
        byte[] bytesMsg = new byte[result.readableBytes()];
        result.readBytes(bytesMsg);
        String resultStr = new String(bytesMsg);
        System.out.println("Client said:" + resultStr);
        String response = "hello world";
        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
        encoded.writeBytes(response.getBytes());
        ctx.writeAndFlush(encoded);
        ctx.fireChannelRead(msg);
    }




}