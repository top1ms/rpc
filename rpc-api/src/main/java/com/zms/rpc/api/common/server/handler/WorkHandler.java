package com.zms.rpc.api.common.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER= LoggerFactory.getLogger(WorkHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        LOGGER.info("处理io的io线程组:"+ctx.channel().eventLoop().parent());
        LOGGER.info("处理io的io线程:"+ctx.channel().eventLoop());

        ByteBuf result = (ByteBuf) msg;
        byte[] bytesMsg = new byte[result.readableBytes()];
        result.readBytes(bytesMsg);
        String resultStr = new String(bytesMsg);
        System.out.println("zms said:" + resultStr);
        String response = "hello badWcl";
        ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
        encoded.writeBytes(response.getBytes());
        ctx.writeAndFlush(encoded);
        ctx.fireChannelRead(msg);
    }




}