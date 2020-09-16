package com.zms.rpc.nio;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

@SuppressWarnings("DuplicatedCode")
public class SingleThreadNIO {
    private final  static Logger LOGGER= LoggerFactory.getLogger(SingleThreadNIO.class);
    private static Selector selector;

    static {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(8080));
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (!Thread.currentThread().isInterrupted()){
            LOGGER.info("server selector one by one process");
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey=iterator.next();
                iterator.remove();
                if(selectionKey.isAcceptable()){
                    processAccept(selectionKey);
                }else if(selectionKey.isReadable()){
                    processReadable(selectionKey);
                }
            }
        }
    }

    public static void processAccept(SelectionKey selectionKey) throws IOException {
            ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            ByteBuffer byteBuffer=ByteBuffer.allocate(1024);
            socketChannel.register(selector,SelectionKey.OP_READ,byteBuffer);

    }
    public static void processReadable(SelectionKey selectionKey) throws IOException {
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer byteBuffer= (ByteBuffer) selectionKey.attachment();
            socketChannel.read(byteBuffer);
            byteBuffer.flip();
            while (byteBuffer.hasRemaining()){
                LOGGER.info("server read data:"+(char)byteBuffer.get());
            }
            byteBuffer.clear();

    }



}
