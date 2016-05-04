package com.example.myapp.logic;

import android.util.Log;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lss on 2016/3/11.
 */
public class Crypto {
    final static int KEY_LENGTH = 256;
    final static int SALT_LENGTH = KEY_LENGTH / 8;
    final static int ITERATION_COUNT = 100;
    final static int IV_LENGTH = 16;
    private SecureRandom random;
    private String password;

    private static Crypto instance;

    public static void init(String password) {
        instance = new Crypto(password);
    }

    public static Crypto getInstance() {
        if (instance == null)
            throw new NullPointerException("Crypto un-initialized !");
        return instance;
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
//        Crypto.init("password");
//        Crypto crypto = Crypto.getInstance();
//        StringBuffer sb = new StringBuffer();
//        File file = new File("./test.txt");
//        sb.append("test;helloword\n");
//        sb.append("lala;pupupupu\n");
//        sb.append("test3;jhhhhhh\n");
//        byte[] buffer = sb.toString().getBytes("UTF-8");
//        crypto.encryptFile(new FileOutputStream(file), buffer);
//
//        byte[] buff = crypto.decryptFile(new FileInputStream(file));
//        String s = new String(buff, "UTF-8");
//        System.out.println(s);

        System.out.println((int)'\n');
//        long l = 1000000;
//        System.out.println((int)l);
    }

    private Crypto(String password) {
        this.password = password;
        this.random = new SecureRandom();
    }

    public void encryptFile(FileOutputStream fileOutputStream, byte[] buffer) {
        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
            // secret key
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            SecretKey key = generateKey(salt);
            if (key == null)
                throw new NullPointerException("null key - password: " + password + " null salt: " + (salt == null));
            // cipher
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
//            int ivLength = cipher.getBlockSize();
//            System.out.println(ivLength);
//            byte[] iv = new byte[ivLength];
//            Log.d("Crypto", "block size: " + ivLength);
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);

            fileOutputStream.write(salt);
//            fileOutputStream.write(ivLength);
            fileOutputStream.write(iv);
            fileOutputStream.flush();

            CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, cipher);
            cipherOutputStream.write(buffer);
            cipherOutputStream.flush();
            cipherOutputStream.close();

            fileOutputStream.close();
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | InvalidAlgorithmParameterException | IOException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public byte[] decryptFile(FileInputStream fileInputStream) {
//        List<String> entries = new ArrayList<>();
        byte[] salt = new byte[SALT_LENGTH];
        byte[] buffer = null;
        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
            int size = (int)fileInputStream.getChannel().size();
            buffer = new byte[size - SALT_LENGTH - IV_LENGTH];
//            buffer = new byte[(int)fileInputStream.getChannel().size()];

            fileInputStream.read(salt);
            SecretKey key = generateKey(salt);
            if (key == null)
                throw new NullPointerException("null key - password: " + password + " null salt: " + (salt == null));

//            int ivLength = fileInputStream.read();
//            byte[] iv = new byte[ivLength];
            byte[] iv = new byte[IV_LENGTH];
            fileInputStream.read(iv);

            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);

            CipherInputStream cis = new CipherInputStream(fileInputStream, cipher);
            cis.read(buffer);
//            BufferedReader br = new BufferedReader(new InputStreamReader(cis));
//            String tmp;
//            while ((tmp = br.readLine()) != null) {
//                entries.add(tmp);
//            }
//            br.close();
//            int tmp ;
//            while ((tmp = cis.read()) != -1)
//                System.out.println((char)tmp);
            cis.close();
            fileInputStream.close();
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidAlgorithmParameterException | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private SecretKey generateKey(byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    public void release(){
        password = null;
        instance = null;
    }


}
