package cn.trelig.filesystem.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class copy {
    static ArrayList<String> temp = new ArrayList<String>();

    //复制文件
    private boolean copyFile(String srcPath, String destDir) {
        boolean flag = false;
        File srcFile = new File(srcPath);
        if (!srcFile.exists()) { // 源文件不存在
            temp.add("源文件不存在");
            return false;
        }
        // 获取待复制文件的文件名
        String fileName = srcPath
                .substring(srcPath.lastIndexOf(File.separator));
        String destPath = destDir + fileName;
        if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
            temp.add("源文件路径和目标文件路径重复!");
            return false;
        }
        File destFile = new File(destPath);
        if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
            temp.add("目标目录下已有同名文件!");
            return false;
        }
        File destFileDir = new File(destDir);
        destFileDir.mkdirs();
        try {
            FileInputStream fis = new FileInputStream(srcPath);
            FileOutputStream fos = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int c;
            while ((c = fis.read(buf)) != -1) {
                fos.write(buf, 0, c);
            }
            fis.close();
            fos.close();
            flag = true;
        } catch (IOException e) {
            //
            System.out.println("Error!");
        }
        return flag;
    }


    //复制文件夹
    private boolean copyDirectory(String srcPath, String destDir) {
        boolean flag = false;

        File srcFile = new File(srcPath);
        if (!srcFile.exists()) { // 源文件夹不存在
            temp.add("源文件夹不存在");
            return false;
        }
        // 获得待复制的文件夹的名字，比如待复制的文件夹为"E://dir"则获取的名字为"dir"
        getInformation getInformation = new getInformation();
        String dirName = getInformation.getDirName(srcPath);
        // 目标文件夹的完整路径
        String destPath = destDir + File.separator + dirName;

        if (destPath.equals(srcPath)) {
            temp.add("目标文件夹与源文件夹重复");
            return false;
        }
        File destDirFile = new File(destPath);
        if (destDirFile.exists()) { // 目标位置有一个同名文件夹
            temp.add("目标位置已有同名文件夹!");
            return false;
        }
        destDirFile.mkdirs(); // 生成目录
        File[] fileList = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
        if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
            flag = true;
        } else {
            for (File temp : fileList) {
                if (temp.isFile()) { // 文件
                    flag = copyFile(temp.getAbsolutePath(), destPath);
                } else if (temp.isDirectory()) { // 文件夹
                    flag = copyDirectory(temp.getAbsolutePath(), destPath);
                }
                if (!flag) {
                    break;
                }
            }
        }
        if (flag) {
            temp.add("复制文件夹成功!");
        }
        return flag;
    }

    //复制文件或文件夹
    public ArrayList<String> copyGeneralFile(String srcPath, String destDir) {

        boolean flag = false;
        File file = new File(srcPath);
        if (!file.exists()) {
            temp.add("源文件或源文件夹不存在!");
        }
        if (file.isFile()) { // 源文件
            //进行文件复制
            temp.add("下面进行文件复制！");
            flag = copyFile(srcPath, destDir);
            if(flag == true) {
                temp.add("文件复制成功！");
            }
            else {
                temp.add("文件复制失败！");
            }
        } else if (file.isDirectory()) {
            //进行文件夹复制!
            flag = copyDirectory(srcPath, destDir);
            if(flag == true) {
                temp.add("文件夹复制成功！");
            }
            else {
                temp.add("文件夹复制失败！");
            }
        }
        return temp;
    }
}
