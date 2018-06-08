package net.lovememo.reflect;

import static org.joor.Reflect.on;

/**
 * Author: lovememo
 * Date: 18-6-8
 */
public class ProxyTest {

    public static void main(String[] args) {
        String raw = "peter";
        System.out.println(raw.substring(2));
        String substringWithProxy = on("java.lang.String")
                .create("Hello World")
                .as(StringProxy.class)      // Create a proxy for the wrapped object
//                .x();
                .substring(6);   // Call a proxy method
        System.out.println(substringWithProxy);
    }
}

interface StringProxy {
    String substring(int beginIndex);
    String x();
}
