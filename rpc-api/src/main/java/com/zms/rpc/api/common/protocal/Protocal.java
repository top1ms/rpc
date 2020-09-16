package com.zms.rpc.api.common.protocal;


import java.io.Serializable;
import java.lang.reflect.Method;

public class Protocal implements Serializable {

    private String interfaceName;
    private Class<?> interfaceType;
    private String methodName;
    private Class<?>[] argumentsType;
    private Object[] arguments;


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getArgumentsType() {
        return argumentsType;
    }

    public void setArgumentsType(Class<?>[] argumentsType) {
        this.argumentsType = argumentsType;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Class<?> getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(Class<?> interfaceType) {
        this.interfaceType = interfaceType;
    }



    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
