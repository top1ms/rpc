package com.zms.rpc.api.common.server;

import com.zms.rpc.api.common.api.UserService;
import com.zms.rpc.api.common.api.UserServiceImpl;
import com.zms.rpc.api.common.protocal.Protocal;
import com.zms.rpc.api.common.protocal.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerOps {
    private final static Logger LOGGER= LoggerFactory.getLogger(ServerOps.class);

    private static Map<String,Object> instances=new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        instances.put(UserService.class.getName(),new UserServiceImpl());
        ServerSocket serverSocket=new ServerSocket();
        serverSocket.bind(new InetSocketAddress(8080));

        while (true){
            Socket socket = serverSocket.accept();
            ObjectInputStream objectInputStream=new ObjectInputStream(socket.getInputStream());
            Protocal protocal= (Protocal) objectInputStream.readObject();
            UserServiceImpl userService= (UserServiceImpl) instances.get(protocal.getInterfaceName());
            Method method=userService.getClass().getDeclaredMethod(protocal.getMethodName(),protocal.getArgumentsType());
            String result= (String) method.invoke(userService,protocal.getArguments());
            LOGGER.info("rpc invocation success:"+result);
            Result rpcResult=new Result();
            rpcResult.setResult(result);
            rpcResult.setCode(200);

            ObjectOutputStream objectOutputStream=new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(rpcResult);

            LOGGER.info("rpc invocation end");





        }

    }
}
