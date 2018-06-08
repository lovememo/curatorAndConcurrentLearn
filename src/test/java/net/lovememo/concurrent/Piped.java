package net.lovememo.concurrent;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * Author: lovememo
 * Date: 18-6-7
 */
public class Piped {
    public static void main(String[] args) throws Exception {
        PipedWriter out1 = new PipedWriter();
        PipedWriter out2 = new PipedWriter();
        PipedReader in1 = new PipedReader();
        PipedReader in2 = new PipedReader();
// 将输出流和输入流进行连接，否则在使用时会抛出IOException
        out1.connect(in1);
        out2.connect(in2);
        Thread printThread1 = new Thread(new Print1(in1), "PrintThread1");
        Thread printThread2 = new Thread(new Print2(in2), "PrintThread2");
        printThread1.start();
        printThread2.start();
        int receive = 0;
        try {
            while ((receive = System.in.read()) != -1) {
                out1.write(receive);
                out2.write(receive);
            }
        } finally {
            out1.close();
            out2.close();
        }
    }

    static class Print2 implements Runnable {
        private PipedReader in;

        public Print2(PipedReader in) {
            this.in = in;
        }

        public void run() {
            int receive = 0;
            try {
                while ((receive = in.read()) != -1) {
                    System.out.print((char) receive);
                }
            } catch (IOException ex) {
            }
        }
    }
    static class Print1 implements Runnable {
        private PipedReader in;

        public Print1(PipedReader in) {
            this.in = in;
        }

        public void run() {
            int receive = 0;
            try {
                String s = "";
                while ((receive = in.read()) != -1) {
                    char x = (char) receive;

                    s += x;

                    if( x == '\n' ) {
                        System.out.print("pirnt1 is printing: " + s );
                        s = "";
                    }
                }
            } catch (IOException ex) {
            }
        }
    }
}
