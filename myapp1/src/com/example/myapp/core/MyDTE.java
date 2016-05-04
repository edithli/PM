package com.example.myapp.core;

import android.util.Log;

import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * Created by lss on 2016/4/1.
 */
public class MyDTE {
    private static int b = 128;
    public static final int BYTE_NUM = b / Byte.SIZE;
    public static final int PWD_ENCODE_LENGTH = 30 * BYTE_NUM;
    public static final int SUB_GRAMMAR_SIZE = 100 * BYTE_NUM;
    private static SecureRandom random = new SecureRandom();

    public MyDTE(){
        random = new SecureRandom();
    }

    public static byte[] encodeProbability(int base, int range, int q) {
        int p = random.nextInt(range) + base;
        return encodeProbability(p, q);
    }

    /**
     * to encode a fraction p/q in which p < q to a large b-bit number
     * @param p the frequency count
     * @param q the cumulative number of frequency
     * x <-$ {0, 1}b
     * r <- x + p - (x mod q)
     * if (r >= 2^b) then
     *  r <- r - q
     * @return the byte array of r
     */
    public static byte[] encodeProbability(int p, int q) {
        byte[] rbytes = new byte[BYTE_NUM];
        random.nextBytes(rbytes);
        BigInteger bp = BigInteger.valueOf(p);
        BigInteger bq = BigInteger.valueOf(q);
        BigInteger x = new BigInteger(rbytes);
        BigInteger r = x.add(bp)
                .subtract(x.mod(bq));
        while (r.compareTo(BigInteger.ONE.shiftLeft(b)) >= 0)
            r = r.subtract(bp);
        byte[] result = r.toByteArray();
        if (result.length != BYTE_NUM)
            Log.e("DTE", "invalid byte length in encoding");
        return r.toByteArray();
    }

    /**
     * decode the large b-bits number in the form of a b-bit byte array
     * @param bytes the bit array of the b-bit number
     * @param q the cumulative count number of a certain rule
     * @return an integer of the frequency count
     */
    public static int decodeProbability(byte[] bytes, int q) {
        BigInteger r = new BigInteger(bytes);
        BigInteger bp = r.mod(BigInteger.valueOf(q));
        return bp.intValue();
    }

    public static byte[] randomBytes(int capacity) {
        byte[] b = new byte[capacity];
        random.nextBytes(b);
        return b;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            byte[] b = encodeProbability(1425, 540, 2020);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            byte[] bb = new byte[BYTE_NUM];
            buffer.get(bb);
            System.out.println(decodeProbability(bb, 2020));
        }
    }
}
