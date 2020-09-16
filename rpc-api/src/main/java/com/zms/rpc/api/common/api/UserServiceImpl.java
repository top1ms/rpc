package com.zms.rpc.api.common.api;

public class UserServiceImpl implements UserService{

    @Override
    public String addName(String name) {
        return "addName success==="+name;
    }
}
