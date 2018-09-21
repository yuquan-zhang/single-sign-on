package com.zhang.yong.sso.util;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.util.HashSet;

public class EncryptUtils {
    private final static String CHARSET = "UTF-8";
    private final static String MD5 = "MD5";

    public static String getMD5Hash(String data) {
        String result = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(MD5);
            byte[] hash = digest.digest(data.getBytes(CHARSET));
            return bytesToHex(hash); // make it printable
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }

    private static String  bytesToHex(byte[] hash) {
        return DatatypeConverter.printHexBinary(hash);
    }

    public static void main(String[] args) {
        HashSet<String> set = new HashSet<String>();
        set.add("aaa");
        set.add("bbb");
        for(String s : set) {
            s += "2";
            System.out.println(s);
        }
        System.out.println(set);
    }
}
