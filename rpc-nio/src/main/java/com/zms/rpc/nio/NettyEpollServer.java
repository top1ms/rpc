package com.zms.rpc.nio;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;


public class NettyEpollServer {

    public static void main(String[] args) throws InterruptedException {
        EpollEventLoopGroup boss=new EpollEventLoopGroup();
        EpollEventLoopGroup work=new EpollEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(boss,work)
                .option(ChannelOption.SO_REUSEADDR,true)
                .handler(new ChannelInitializer<EpollServerSocketChannel>() {
                    @Override
                    protected void initChannel(EpollServerSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<Object>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
//                                ctx.executor().parent()
                                System.out.println(("安全认证开始:"+Thread.currentThread().getName()));
                            }
                        });
                    }
                })
                .childHandler(new ChannelInitializer<EpollSocketChannel>() {
                    @Override
                    protected void initChannel(EpollSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<Object>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(("数据读取:"+Thread.currentThread().getName()));
                            }
                        });
                    }
                });
        ChannelFuture channelFuture=serverBootstrap.bind(8080).sync();
        channelFuture.channel().closeFuture().sync();

    }
}
