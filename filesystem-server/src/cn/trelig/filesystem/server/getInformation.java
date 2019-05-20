package cn.trelig.filesystem.server;

import java.io.File;
import java.util.ArrayList;

public class getInformation {
    //得到目录名字
    public String getDirName(String dir) {
        if (dir.endsWith(File.separator)) { // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
            dir = dir.substring(0, dir.lastIndexOf(File.separator));
        }
        return dir.substring(dir.lastIndexOf(File.separator) + 1);
    }

    //获取文件列表
    public ArrayList<String> getFiles(String path) {
        File file = new File(path);
        File[] tempList = file.listFiles();
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                temp.add(tempList[i].getName());
            }
            if (tempList[i].isDirectory()) {
                temp.add(tempList[i].getName());
            }
        }
        return temp;
    }
}
