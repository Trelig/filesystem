package cn.trelig.filesystem.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class create {
    //创建文件
    public String createFile(String path, String fileName) {
        String temp = null;
        String str = path + fileName;
        File file = new File(str);
        if (file.exists()) {
            //System.out.println("该文件已存在。");
            temp = ("文件 " + fileName + " 已存在。");
        } else {
            //System.out.println("该文件不存在，创建该文件。");
            temp = ("文件 " + fileName + " 不存在，创建该文件。");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return temp;
    }

    //创建文件夹
    public String createFolder(String path, String folderName){
        String temp = null;
        String str = path + folderName;
        File dir = new File(str);
        if (dir.exists()) {
            //System.out.println("该文件夹已存在。");
            temp = "文件夹 " + folderName +" 已存在。";
        } else {
            //System.out.println("该文件夹不存在，创建该文件夹。");
            temp = "文件夹 " + folderName + " 不存在，创建该文件夹。";
            dir.mkdirs();
        }
        return temp;
    }
}
