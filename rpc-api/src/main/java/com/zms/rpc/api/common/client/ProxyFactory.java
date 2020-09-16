package com.zms.rpc.api.common.client;

import com.zms.rpc.api.common.protocal.Protocal;
import com.zms.rpc.api.common.protocal.Result;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("ALL")
public class ProxyFactory {

    public static <T>T getProxy(Class<?> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(!method.getName().equals("toString")){
                    Socket socket=new Socket("127.0.0.1",8080);
                    Protocal protocal=new Protocal();
                    protocal.setInterfaceName(clazz.getName());
                    protocal.setInterfaceType(clazz.getClass());
                    protocal.setMethodName(method.getName());
                    protocal.setArguments(args);
                    protocal.setArgumentsType(method.getParameterTypes());
                    ObjectOutputStream serilizer=new ObjectOutputStream(socket.getOutputStream());
                    serilizer.writeObject(protocal);

                    //stimulate async invoke
                    CompletableFuture<String> completableFuture=new CompletableFuture<>();

                    Runnable runnable=new Runnable() {
                        @Override
                        public void run() {
                            ObjectInputStream deserilizer= null;
                            try {
                                deserilizer = new ObjectInputStream(socket.getInputStream());
                                Result result= (Result) deserilizer.readObject();
                                completableFuture.complete((String) result.getResult());
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    };
                    Thread thread=new Thread(runnable);
                    thread.start();

//
//
//
//
//
//
//
                    return completableFuture;
                }
                return null;

            }
        });

    }
}
