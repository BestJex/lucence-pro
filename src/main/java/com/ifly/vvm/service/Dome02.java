package com.ifly.vvm.service;

public class Dome02 {
    public static void main(String[] args) {
        String indexDir = "D:\\\\usr\\\\local\\\\vvm\\\\lucence\\\\demo1";
        String q = "Lucene";
        try {
            IndexUse.search(indexDir, q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
