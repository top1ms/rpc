package com.zms.rpc.api.common.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.Future;


public class ServerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger LOGGER= LoggerFactory.getLogger(ServerHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.info("acceptor当前线程池:"+ctx.channel().eventLoop().parent());
        LOGGER.info("acceptor当前线程:"+Thread.currentThread().getName());
        LOGGER.info("模拟auth认证");
        Promise<Boolean> promise= (Promise<Boolean>) ctx.channel().eventLoop().parent().submit(()->{
            LOGGER.info("auth认证当前线程池:"+ctx.channel().eventLoop().parent());
            LOGGER.info("auth当前线程:"+Thread.currentThread().getName());
            LOGGER.info("++++++++++++++++++++++++++++++++++");
            //拿到客户链接的ip地址
            SocketAddress socketAddress = ctx.channel().remoteAddress();
            //模拟白黑名单过滤
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        });
        promise.addListener((future)->{
            Object object = future.get();
            if((Boolean) object){
                //认证成功 拦截器链继续传播 交给ServerAcceptor处理 交给ChildrenGroup注册read事件监听
                ctx.fireChannelRead(msg);
            }
        });


    }
}
