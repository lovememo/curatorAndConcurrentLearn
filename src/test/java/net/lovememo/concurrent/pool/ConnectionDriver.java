package net.lovememo.concurrent.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * Author: lovememo
 * Date: 18-6-7
 */
public class ConnectionDriver {
    //创建一个Connection的代理，在commit时休眠100毫秒
    public static final Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionDriver.class.getClassLoader(),
                new Class<?>[]{Connection.class}, (proxy, method, args) -> {
                    if (method.getName().equals("commit")) {
                        TimeUnit.MILLISECONDS.sleep(100);
                    }
                    return null;
                });
    }
}