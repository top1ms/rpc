package com.zms.rpc.api.common.api;

import java.util.concurrent.CompletableFuture;

public interface UserService  {

    String addName(String name);

    CompletableFuture<String> syncAddName(String name);
}
