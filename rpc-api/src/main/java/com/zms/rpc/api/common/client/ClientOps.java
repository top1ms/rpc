package com.zms.rpc.api.common.client;

import com.zms.rpc.api.common.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ClientOps {

    private final static Logger LOGGER= LoggerFactory.getLogger(ClientOps.class);
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        UserService userService=ProxyFactory.getProxy(UserService.class);
//        String result=userService.addName("zms");
        CompletableFuture<String> completableFuture=userService.syncAddName("bfq");
//        LOGGER.info("client rpc invocation result:"+result);
        LOGGER.info("client sync invocation begin");

        while (!completableFuture.isDone()){
            LOGGER.info("client sync invocation await");
        }
        LOGGER.info("client rpc sync invocation result:"+completableFuture.get());



    }
}
