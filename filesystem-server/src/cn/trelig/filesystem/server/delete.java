package cn.trelig.filesystem.server;

import java.io.File;
import java.util.ArrayList;

public class delete {
    //删除文件
    public ArrayList<String> deleteFile(String path, String fileName) {
        ArrayList<String>  temp = new ArrayList<String>();
        String str = path + fileName;
        File file = new File(str);
        if (file.exists()) {
            //文件存在
            String tString = file.getAbsolutePath() + "存在";
            temp.add(tString);
            file.delete();
            //System.out.println("该文件已删除。");
            temp.add("该文件已删除。");
        } else {
            //文件不存在
            temp.add("文件不存在。");
        }
        return temp;
    }

    //删除文件夹
    public ArrayList<String> deleteFolder(String path, String folderName) {
        boolean flag = true;
        ArrayList<String> temp = new ArrayList<String>();
        String str = path + folderName;
        File dirfile = new File(str);
        if(!dirfile.isDirectory()) {
            flag = true;
            temp.add("文件夹不存在！");
        }
        File[] files = dirfile.listFiles();
        for (File file : files) {
            if(file.isFile()) {
                ArrayList<String> tempFile = deleteFile(str + "\\", file.getName());
                for(String string : tempFile) {
                    temp.add(string);
                }
            }else if (file.isDirectory()) {
                ArrayList<String> tempDir = deleteFolder(str + "\\", file.getName());
                for(String string : tempDir) {
                    temp.add(string);
                }
            }
            if(!flag) {
                break;
            }
        }
        flag = dirfile.delete();
        if(flag) {
            temp.add("删除文件夹成功！");
        }
        else {
            temp.add("删除文件夹失败！");
        }
        return temp;
    }

}
