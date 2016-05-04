package com.example.myapp.core;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

/**
 * Created by lss on 2016/3/16.
 *
 * encode the 96 printable ASCII characters
 */
public class UNIF {
    private static final int CHARNO = 96;
    private static final int BIT_NO = 127;
//    private static final int MAX_R = (int)(Math.pow(2, BIT_NO) / CHARNO);
    private static final int BYTES_NO = 128 / Byte.SIZE;
    private static final int R_BIT = BIT_NO - 6;
    private static SecureRandom random = new SecureRandom();

    private static final BigInteger MAX_R = BigInteger.ONE.shiftLeft(127);
    private static final BigInteger MINUS_P = (BigInteger.ONE.shiftLeft(125)).multiply(BigInteger.valueOf(3));

    /**
     * encode each character into a 128-bit integer
     * @param input
     * @return
     */
    public static byte[] encode(String input) {
        char[] chars = input.toCharArray();
        byte[] result = new byte[chars.length * BYTES_NO];
        ByteBuffer buffer = ByteBuffer.wrap(result);
        int seta;
        for (char c : chars) {
            if (c == '\n') seta = 0;
            else seta = c - 31;
//            Log.d("UNIF", "seta: " + seta);
//            System.out.println("seta: " + seta);
            BigInteger r = new BigInteger(R_BIT, random);
            BigInteger xi = r.multiply(BigInteger.valueOf(CHARNO))
                             .add(BigInteger.valueOf(seta));
            while (xi.compareTo(MAX_R) > 0)
                xi = xi.subtract(MINUS_P);
//            System.out.println("encode - xi: " + xi.toString(16));
            byte[] tmp = xi.toByteArray();
            int i = BYTES_NO - tmp.length;
            while (i > 0) {
                buffer.put((byte)0);
                i--;
            }
            buffer.put(tmp);
//            long r = random.nextLong();
//            while (r >= MAX_R)
//                r = random.nextLong();
//            long xi = seta + CHARNO * r;
//            buffer.putLong(xi);
        }
        buffer.flip();
        return buffer.array();
    }

    public static String decode(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        for (int i = 0; i < bytes.length; i+= BYTES_NO) {
            byte[] tmp = new byte[BYTES_NO];
            buffer.get(tmp);
            BigInteger xi = new BigInteger(tmp);
//            System.out.println("decode - xi: " + xi.toString(16));
            BigInteger seta = xi.remainder(BigInteger.valueOf(CHARNO));
//            System.out.println("decode - seta: " + seta.toString());
            if (seta.compareTo(BigInteger.valueOf(0)) == 0)
                sb.append('\n');
            else sb.append((char)(seta.intValue() + 31));
//            Long xi = buffer.getLong();
//            int seta = (int) (xi % CHARNO);
//            System.out.println("decode seta: " + seta);
//            if (seta == 0) sb.append('\n');
//            else sb.append((char) (seta + 31));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String input = "checksum\n12345789;usera;password1\n1234764327888;usera;password2\n";
        System.out.println(decode(encode(input)));
    }
}
