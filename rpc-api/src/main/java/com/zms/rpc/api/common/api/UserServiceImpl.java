package com.zms.rpc.api.common.api;

import java.util.concurrent.CompletableFuture;

public class UserServiceImpl implements UserService{


    @Override
    public String addName(String name) {
        return "addName success==="+name;
    }

    @Override
    public CompletableFuture<String> syncAddName(String name) {
        CompletableFuture<String> completableFuture=new CompletableFuture<>();
        try {
            Thread.sleep(5000);
            completableFuture.complete(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return completableFuture;
    }


}
