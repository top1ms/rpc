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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("DuplicatedCode")
public class MultiProcessThreadNIO {
    private final static Logger LOGGER= LoggerFactory.getLogger(MultiProcessThreadNIO.class);

    private final static AtomicInteger num=new AtomicInteger();
    private final static Random random=new Random();

    private static List<NioThread> nioThreads=new ArrayList<>();


    public static void main(String[] args) throws IOException {
        //accept
        Selector selector=Selector.open();

        //one fd bind one port
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();

        serverSocketChannel.configureBlocking(false);



        serverSocketChannel.register(selector, 0);


        serverSocketChannel.bind(new InetSocketAddress(8080));

        //netty初始化 pipelLine里面的handler


        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        //io read write
        Selector work1=Selector.open();
        Selector work2=Selector.open();

        NioThread nioThread1=new NioThread(work1);
        NioThread nioThread2=new NioThread(work2);
        nioThread1.start();
        nioThread2.start();
        nioThreads.add(nioThread1);
        nioThreads.add(nioThread2);


        while (!Thread.currentThread().isInterrupted()){
            LOGGER.info("server selector ");
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey=iterator.next();
                iterator.remove();
                if(selectionKey.isAcceptable()){
                    processAccept(selectionKey);
                }
            }
        }




    }

    private static void processAccept(SelectionKey selectionKey) throws IOException {
        //one thread process accept
        ServerSocketChannel serverSocketChannel= (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel=serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        //we maybe have some auth process example ip filter
        //current mode is singleThread process accept + ipfilter


        //multi thread process read/write
        BlockingQueue<SocketChannel> socketChannelBlockingQueue=getBlockQueueThroughThreads(nioThreads);
        socketChannelBlockingQueue.add(socketChannel);
//        Selector work=getSelectorThroughTreads(nioThreads);
//        socketChannel.register(work,SelectionKey.OP_READ);

    }


    private static NioThread getWordThread(List<NioThread> nioThreads){
        int size=nioThreads.size();
        int num= random.nextInt(size);
        return nioThreads.get(num);

    }

    private static BlockingQueue<SocketChannel> getBlockQueueThroughThreads(List<NioThread> nioThreads){
        return getWordThread(nioThreads).getQueue();
    }

    private static Selector getSelectorThroughTreads(List<NioThread> nioThreads){
        return getWordThread(nioThreads).getSelector();
    }






    public static class NioThread extends Thread{
        // one thread bind one selector
        private Selector selector;

        private BlockingQueue<SocketChannel> queue;

        private ByteBuffer byteBuffer;

        public NioThread(Selector selector){
            this.selector=selector;
            this.queue= new ArrayBlockingQueue<>(100);
            this.byteBuffer=ByteBuffer.allocate(1024);
        }

        @Override
        public void run() {
            while (true){
                try {
                    SocketChannel socketChanel = queue.poll(100, TimeUnit.NANOSECONDS);
                    if(socketChanel!=null){
                        socketChanel.register(selector,SelectionKey.OP_READ);
                    }
                    selector.select(100);
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()){
                        SelectionKey selectionKey=iterator.next();
                        SocketChannel socketChannel = (SocketChannel)selectionKey.channel();
                        if(selectionKey.isReadable()){
                            socketChannel.read(byteBuffer);
                            byteBuffer.flip();
                            while (byteBuffer.hasRemaining()){
                                LOGGER.info(String.valueOf((char)byteBuffer.get()));
                            }
                            byteBuffer.clear();
                        }
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

        public Selector getSelector() {
            return selector;
        }

        public void setSelector(Selector selector) {
            this.selector = selector;
        }

        public BlockingQueue<SocketChannel> getQueue() {
            return queue;
        }

        public void setQueue(BlockingQueue<SocketChannel> queue) {
            this.queue = queue;
        }
    }

}
