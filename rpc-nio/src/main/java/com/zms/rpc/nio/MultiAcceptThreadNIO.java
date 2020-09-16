package com.zms.rpc.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("DuplicatedCode")
public class MultiAcceptThreadNIO {

    private final static Logger LOGGER= LoggerFactory.getLogger(MultiAcceptThreadNIO.class);

    private final static AtomicInteger num=new AtomicInteger();
    private final static Random random=new Random();

    private final static Set<SelectionKey> hashSet=new HashSet<>();


    private static List<NioThread> workThread=new ArrayList<>();

    private final static ThreadPoolExecutor excutor=new ThreadPoolExecutor(2,4,0,TimeUnit.SECONDS,new ArrayBlockingQueue<>(100));

    public static void main(String[] args) throws IOException {

        Selector selector=Selector.open();

        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8080));

        serverSocketChannel.register(selector,SelectionKey.OP_ACCEPT);

        NioThread acceptor=new NioThread(selector,true);

        acceptor.start();

        Selector selector1=Selector.open();
        Selector selector2=Selector.open();


        NioThread work1=new NioThread(selector1);
        NioThread work2=new NioThread(selector2);

        workThread.add(work1);
        workThread.add(work2);

        work1.start();
        work2.start();







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

        private boolean isBossGroup;


        public NioThread(Selector selector,boolean isBossGroup){
            this.selector=selector;
            this.queue= new ArrayBlockingQueue<>(100);
            this.isBossGroup=true;
        }

        public NioThread(Selector selector){
            this.selector=selector;
            this.queue= new ArrayBlockingQueue<>(100);
            this.byteBuffer=ByteBuffer.allocate(1024);
        }

        @Override
        public void run() {
            while (true){
                if(isBossGroup){
                    try {
                        boss();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else work();


            }
        }
        public void boss() throws IOException {
            //still one thread listen one fd process accept event
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()){
                SelectionKey selectionKey=iterator.next();
                iterator.remove();

                //beacause Java use the epoll mode is LT
                //if fd is not processed
                //the same fd will back again

                if(!hashSet.contains(selectionKey)){
                    hashSet.add(selectionKey);
                    if(selectionKey.isAcceptable()){
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();

                        excutor.execute(()->{
                            final SocketChannel socketChannel;
                            try {
                                //stimulate auth link

                                LOGGER.info(Thread.currentThread().getName()+"---"+"accept before");

                                Thread.sleep(5000);

                                socketChannel = serverSocketChannel.accept();
                                LOGGER.info(Thread.currentThread().getName()+"---"+"accept after");

                                if(socketChannel!=null){
                                    socketChannel.configureBlocking(false);
                                    //dispatch socketChannel to I/O thread poll
                                    BlockingQueue<SocketChannel> socketChannelBlockingQueue=getBlockQueueThroughThreads(workThread);
                                    socketChannelBlockingQueue.add(socketChannel);
                                }
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }

                        });

                    }

                }







            }
        }

        public void work(){
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
