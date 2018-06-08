package net.lovememo.concurrent;

import java.io.*;

/**
 * Author: lovememo
 * Date: 18-6-7
 */
public class PipedStream {
    private static final int BUFFER_SIZE = 1024;
    public static void main(String[] args) throws Exception {
        PipedOutputStream out1 = new PipedOutputStream();
        PipedOutputStream out2 = new PipedOutputStream();
        PipedInputStream in1 = new PipedInputStream();
        PipedInputStream in2 = new PipedInputStream();
        // 将输出流和输入流进行连接，否则在使用时会抛出IOException
        out1.connect(in1);
        out2.connect(in2);
        Thread printThread1 = new Thread(new Print1(in1, out2), "PrintThread1");
        Thread printThread2 = new Thread(new Print2(in2), "PrintThread2");
        printThread1.start();
        printThread2.start();
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (System.in.read(buffer) != -1) {
                out1.write(buffer);
                buffer = new byte[BUFFER_SIZE];
            }
        } finally {
            out1.close();
        }
    }

    static class Print1 implements Runnable {
        private PipedInputStream in;
        private PipedOutputStream out;

        public Print1(PipedInputStream in, PipedOutputStream out) {
            this.in = in;
            this.out = out;
        }

        public void run() {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                while (in.read(buffer) != -1) {
                    out.write(buffer);
                }
            } catch (IOException ex) {
            }
        }
    }

    static class Print2 implements Runnable {


        private PipedInputStream in;

        public Print2(PipedInputStream in) {
            this.in = in;
        }

        public void run() {
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                ByteCache cache = new ByteCache();
                while (in.read(buffer) != -1) {
                    cache.append(buffer);
                    if(cache.isFrame()) {
                        System.out.print(new String(cache.getData()));
                        System.out.println("---print2 get data from print 1 , not from user -");
                        cache.clear();
                    }
                }
            } catch (IOException ex) {
            }
        }
    }

    static class ByteCache {
        public ByteCache() {

        }

        private byte[] cache = new byte[0];

        public void append(byte[] src) {
            int srcLength = getLength(src);
            byte[] temp = new byte[cache.length + srcLength];
            System.arraycopy(cache,0, temp,0, cache.length);
            System.arraycopy(src,0, temp, cache.length, srcLength);
            cache = temp;
        }

        private int getLength(byte[] src) {
            for(int i=src.length-1; i>=0; i--) {
                if('\n' == src[i]) {
                    return i + 1;
                }
            }
            return src.length;
        }


        private int getLength() {
            return getLength(this.cache);
        }

        private boolean isFrame(byte[] src) {
            int length = getLength(src);
            if(length < src.length) {
                return true;
            }
            if(length == src.length && src[src.length-1] == '\n') {
                return true;
            }
            return false;
        }

        public boolean isFrame() {
            return isFrame(cache);
        }

        public byte[] getData() {
            return this.cache;
        }

        public void clear() {
            this.cache = new byte[0];
        }

        public static void main(String[] args) {
            ByteCache byteCache = new ByteCache();
            byteCache.append("$hello!".getBytes());
            byteCache.append(new byte[]{0,0,0,0,0,0,0});
            byteCache.append("_ world~\n".getBytes());
            byteCache.append(new byte[]{0,0,0,0,0,0,0});
            System.out.println(new String(byteCache.getData()));
            System.out.println(byteCache.isFrame());

            byteCache.clear();

            byteCache.append("124".getBytes());
            System.out.println(byteCache.getLength());
            byteCache.append(new byte[] {'\n'});
            System.out.println(byteCache.getLength());
            byteCache.append(new byte[] {0,0,0,0});
            System.out.println(byteCache.getLength());

        }
    }

}
