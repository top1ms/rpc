package com.zms.rpc.api.common.client;

import com.zms.rpc.api.common.api.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientOps {

    private final static Logger LOGGER= LoggerFactory.getLogger(ClientOps.class);
    public static void main(String[] args) {
        UserService userService=ProxyFactory.getProxy(UserService.class);
        String result=userService.addName("zms");
        LOGGER.info("client rpc invocation result:"+result);


    }
}
