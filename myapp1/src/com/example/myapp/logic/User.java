package com.example.myapp.logic;

import android.content.Context;
import android.util.Log;
import com.example.myapp.core.HoneyVault;
import com.example.myapp.core.SubGrammar;
import com.example.myapp.core.TrainedGrammar;
import com.example.myapp.util.Constants;

import java.io.*;
import java.util.*;

/**
 * Created by lss on 2016/2/29.
 */
public class User {
    private Context context;
    private String username;
    private String checksum;
    private Map<String, Content> websiteMap;
    private Map<String, Content> cardMap;
    private Map<String, Content> noteMap;
    private boolean webNeedStore = false, cardNeedStore = false, noteNeedStore = false;
    private Crypto crypto;
    private HoneyVault hv;
    // private encryption/decryption key

    private static User instance;

    public static User getInstance(String username, Context context) {
        if (instance == null && context == null)
            throw new NullPointerException("Problem with User initialization!");
        if (instance == null)
            instance = new User(username, context);
        return instance;
    }

    public void register(String checksum) {
        this.checksum = checksum;
        websiteMap = new LinkedHashMap<>();
        cardMap = new HashMap<>();
        noteMap = new HashMap<>();
        crypto = Crypto.getInstance();
        hv = new HoneyVault(TrainedGrammar.getInstance(), SubGrammar.getInstance());
        // @TODO: to store website & card & note separately without using any information of master password
        // @TODO: to store checksum using master password
        storeWebsites();
        storeCard();
        storeNote();
        Log.d("User", "user - " + username + " registered");
    }

    private User(String username, Context context) {
        this.username = username;
        this.context = context;
        websiteMap = cardMap = noteMap = null;
        crypto = Crypto.getInstance();
        hv = new HoneyVault(TrainedGrammar.getInstance(), SubGrammar.getInstance());
        Log.d("User", "file dir: " + context.getFilesDir().getAbsolutePath());
    }

    public void addWebsite(String domain, String nickname, String pwd) {
        if (websiteMap == null)
            getWebsiteMap();
        websiteMap.put(domain, new Content(nickname, pwd));
        webNeedStore = true;
//        if (!websiteMap.containsKey(domain)) {
//            websiteMap.put(domain, new Content(nickname, pwd));
//            storeEntry(websiteFileName(), websiteEntry(domain, nickname, pwd));
//        }else if (!pwd.equals(websiteMap.get(domain))){
//            websiteMap.put(domain, new Content(nickname, pwd));
//            webNeedStore = true;
//        }
    }

    public void addCard(String nickname, String cardName, String pwd) {
        if (cardMap == null)
            getCardMap();
        cardMap.put(nickname, new Content(cardName, pwd));
        cardNeedStore = true;
//        if (!cardMap.containsKey(cardName)) {
//            cardMap.put(nickname, new Content(cardName, pwd));
//            storeEntry(cardFileName(), cardEntry(nickname, cardName, pwd));
//        }else if (!pwd.equals(cardMap.get(cardName))){
//            cardMap.put(nickname, new Content(cardName, pwd));
//            cardNeedStore = true;
//        }
    }

    public void addNote(String noteName, String content) {
        if (noteMap == null)
            getNoteMap();
        noteMap.put(noteName, new Content(content));
        noteNeedStore = true;
//        if (!noteMap.containsKey(noteName)) {
//            noteMap.put(noteName, new Content(content));
//            storeEntry(noteFileName(), noteEntry(noteName, content));
//        }else if (!content.equals(noteMap.get(noteName))){
//            noteMap.put(noteName, new Content(content));
//            noteNeedStore = true;
//        }
    }

    public void removeWebsite(String domain) {
        if (websiteMap == null)
            getWebsiteMap();
        if (websiteMap.containsKey(domain)) {
            websiteMap.remove(domain);
            webNeedStore = true;
        }
    }

    public void removeCard(String cardName) {
        if (cardMap == null)
            getCardMap();
        if (cardMap.containsKey(cardName)) {
            cardMap.remove(cardName);
            cardNeedStore = true;
        }
    }

    public void removeNote(String noteName) {
        if (noteMap == null)
            getNoteMap();
        if (noteMap.containsKey(noteName)) {
            noteMap.remove(noteName);
            noteNeedStore = true;
        }
    }

    public Map<String, Content> getWebsiteMap() {
        if (websiteMap != null && checksum != null)
            return websiteMap;
//        websiteMap = new HashMap<>();
//        Log.d("User", "file path: " + context.getFilesDir().toString());
        try {
            FileInputStream fis = context.openFileInput(websiteFileName());
//            Crypto crypto = Crypto.getInstance();
            websiteMap = decodeWebsite(crypto.decryptFile(fis));

//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String tmp;
//            checksum = br.readLine();
//            Log.d("User", checksum);
//            while ((tmp = br.readLine()) != null) {
//                StringTokenizer st = new StringTokenizer(tmp, ";");
//                Log.d("User", "split website entry: " + tmp);
//                websiteMap.put(st.nextToken(), new Content(st.nextToken(), st.nextToken()));
//            }
//            fis.close();
//            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return websiteMap;
    }

    public void storeWebsites(){
        if (checksum == null || websiteMap == null)
            return;
        // write the new map back into the userfile
        try {
            FileOutputStream fos = context.openFileOutput(websiteFileName(), Context.MODE_PRIVATE);
//            Crypto crypto = Crypto.getInstance();
            crypto.encryptFile(fos, encodeWebsite());
//            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
//            bw.write(checksum);
//            bw.newLine();
//            if (websiteMap != null)
//                for (Map.Entry<String, Content> entry : websiteMap.entrySet()) {
//                    bw.write(websiteEntry(entry.getKey(), entry.getValue().getKey(), entry.getValue().getValue()));
//                    bw.newLine();
//                    Log.d("User", "write in: " + entry.getKey());
//                }
//            bw.close();
//            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Content> getCardMap(){
        if (cardMap != null)
            return cardMap;
//        cardMap = new HashMap<>();
        try {
            FileInputStream fis = context.openFileInput(cardFileName());
            byte[] buffer = crypto.decryptFile(fis);
            cardMap = decodeCard(buffer);
//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String tmp;
//            while ((tmp = br.readLine()) != null){
//                String[] st = tmp.split(";", 3);
//                Log.d("User", "split card entry: " + st[0] + " - tmp: " + tmp);
//                cardMap.put(st[0], new Content(st[1], st[2]));
//            }
//            br.close();
//            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cardMap;
    }

    public void storeCard(){
        if (checksum == null || cardMap == null)
            return;
        try {
            FileOutputStream fos = context.openFileOutput(cardFileName(), Context.MODE_PRIVATE);
            crypto.encryptFile(fos, encodeCard());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Content> getNoteMap(){
        if (noteMap != null)
            return noteMap;
//        noteMap = new HashMap<>();
        try {
            FileInputStream fis = context.openFileInput(noteFileName());
            noteMap = decodeNote(crypto.decryptFile(fis));
//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String tmp;
//            while ((tmp = br.readLine()) != null){
//                String[] st = tmp.split(";", 2);
//                Log.d("User", "split note entry: " + st[0] + " - tmp: " + tmp);
//                String noteContent = st[1].replaceAll(Constants.NEWLINE, "\n");
//                noteMap.put(st[0], new Content(noteContent));
//            }
//            br.close();
//            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return noteMap;
    }

    public void storeNote(){
//        storeMap(noteMap, noteFileName());
        if (noteMap == null)
            return;
        try {
            crypto.encryptFile(context.openFileOutput(noteFileName(), Context.MODE_PRIVATE), encodeNote());
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

//    private void storeEntry(String fileName, String content) {
//        try {
//            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_APPEND);
//            Log.d("User", "store entry: " + content);
//            fos.write(content.getBytes());
//            fos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    public Map<String, String> getCardMap() {
//        if (cardMap != null)
//            return cardMap;
//        cardMap = new HashMap<>();
//        try {
//            FileInputStream fis = context.openFileInput(cardFileName());
//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String tmp;
//            while ((tmp = br.readLine()) != null){
//                StringTokenizer st = new StringTokenizer(tmp, ";");
//                cardMap.put(st.nextToken(), st.nextToken());
//            }
//            br.close();
//            fis.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return cardMap;
//    }

//    public Map<String, String> getNoteMap() {
//        if (noteMap != null)
//            return noteMap;
//        noteMap = new HashMap<>();
//        try {
//            FileInputStream fis = context.openFileInput(noteFileName());
//            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//            String tmp;
//            while ((tmp = br.readLine()) != null){
//                String[] st = tmp.split(";", 2);
//                noteMap.put(st[0], st[1]);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return noteMap;
//    }

    public String websiteFileName() {
        return "user-" + username + "-website.txt";
    }

    public String cardFileName() {
        return "user-" + username + "-card.txt";
    }

    public String noteFileName() {
        return "user-" + username + "-note.txt";
    }

    public void release(){
        instance = null;
        checksum = null;
    }

    public void store(){
        if (webNeedStore) storeWebsites();
        if (cardNeedStore) storeCard();
        if (noteNeedStore) storeNote();
    }

    public String getUsername() {
        return username;
    }

    public String getChecksum(){
        if (checksum == null)
            getWebsiteMap();
        return checksum;
    }

    public void removeUser(){
        context.deleteFile(websiteFileName());
        context.deleteFile(cardFileName());
        context.deleteFile(noteFileName());
        release();
    }

//    private String websiteEntry(String domain, String nickname, String pwd) {
//        return domain + ";" + nickname + ";" + pwd + "\n";
//    }
//
//    private String cardEntry(String nickname, String cardno, String pwd) {
//        return nickname + ";" + cardno + ";" + pwd + "\n";
//    }
//
//    private String noteEntry(String noteName, String noteContent) {
//        return noteName + ";" + noteContent.replaceAll("\n", Constants.NEWLINE) + "\n";
//    }

    private byte[] encodeWebsitePassword() {
        if (websiteMap == null || websiteMap.isEmpty())
            return null;
        for (Map.Entry<String, Content> entry : websiteMap.entrySet()) {
            hv.addPassword(entry.getValue().getValue());
            entry.getValue().deleteValue();
        }
        return hv.encodeVault();
    }

    private void decodeWebsitePassword(byte[] b) {
        if (websiteMap == null || websiteMap.isEmpty())
            return;
        List<String> pwdList = hv.decodeVault(b);
        int i = 0;
        for (Map.Entry<String, Content> entry: websiteMap.entrySet()) {
            entry.getValue().setValue(pwdList.get(i));
            i++;
        }
    }

    private byte[] encodeWebsite() throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        sb.append(checksum).append("\n");
        if (websiteMap != null)
            for (Map.Entry<String, Content> entry : websiteMap.entrySet()) {
                sb.append(entry.getKey()).append(";").append(entry.getValue().getKey())
                        .append(";").append(entry.getValue().getValue()).append("\n");
            }
        return sb.toString().getBytes("UTF-8");
    }

    private Map<String, Content> decodeWebsite(byte[] buffer) throws UnsupportedEncodingException {
//        Log.d("User", "decode website: null buffer ?" + (buffer == null));
        Map<String, Content> map = new LinkedHashMap<>();
        String s = new String(buffer, "UTF-8");
//        Log.d("User", "decode string: " + s);
        StringTokenizer st = new StringTokenizer(s, "\n");
        checksum = st.nextToken();
//        System.out.println(checksum);
        Log.d("User", "decode - checksum: " + checksum);
        String tmp;
        while (st.hasMoreElements()) {
            tmp = st.nextToken();
            if (!tmp.equals("") && tmp.contains(";")){
                String[] tmpList = tmp.split(";");
                map.put(tmpList[0], new Content(tmpList[1], tmpList[2]));
//                System.out.println(tmpList[0] + " " + tmpList[1] + " " + tmpList[2]);
            }
        }
        return map;
    }

    private byte[] encodeCard() throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        sb.append(checksum + "\n");
        if (cardMap != null)
            for (Map.Entry<String, Content> entry : cardMap.entrySet()) {
                sb.append(entry.getKey() + ";" + entry.getValue().getKey() + ";" + entry.getValue().getValue() + "\n");
            }
        return sb.toString().getBytes("UTF-8");
    }

    private Map<String, Content> decodeCard(byte[] buffer) throws UnsupportedEncodingException {
        return decodeWebsite(buffer);
    }

    private byte[] encodeNote() throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        if (noteMap != null)
            for (Map.Entry<String, Content> entry : noteMap.entrySet()) {
                sb.append(entry.getKey() + ";" + entry.getValue().getKey().replaceAll("\n", Constants.NEWLINE) + "\n");
            }
        return sb.toString().getBytes("UTF-8");
    }

    private Map<String, Content> decodeNote(byte[] buffer) throws UnsupportedEncodingException {
        Map<String, Content> map = new HashMap<>();
        String s = new String(buffer, "UTF-8");
        StringTokenizer st = new StringTokenizer(s, "\n");
        String tmp;
        while (st.hasMoreElements()) {
            tmp = st.nextToken();
            if (!tmp.equals("") && tmp.contains(";")){
                String[] tmpList = tmp.split(";");
                map.put(tmpList[0], new Content(tmpList[1].replaceAll(Constants.NEWLINE, "\n")));
//                System.out.println(tmpList[0] + " " + tmpList[1] + " " + tmpList[2]);
            }
        }
        return map;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
//        Map<String, Content> map = new HashMap<>();
//        map.put("www.baidu.com", new Content("usera", "password123"));
//        map.put("www.cn.com", new Content("usera", "password321"));
//        byte[] tmp = encodeWebsite(map, "checksum");
//        map = decodeWebsite(tmp);
//        for (Map.Entry<String, Content> entry: map.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue().getKey() + " " + entry.getValue().getValue());
//        }
    }
}