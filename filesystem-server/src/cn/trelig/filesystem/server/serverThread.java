package cn.trelig.filesystem.server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class serverThread implements Runnable {
    public final static int bytesize = 2048;
    private Socket client = null;

    public serverThread(Socket client) {
        this.client = client;
    }

    public void refresh(PrintStream out, String systemPath){
        ArrayList<String> files = (new getInformation()).getFiles(systemPath);
//        out.println("refresh");
        out.println(files);
//        for (String filename : files){
//            out.println(filename);
//        }
    }

    String str = null;
    @Override
    public void run() {
        try {
            File directory = new File("");//设定为当前文件夹
            String systemPath = directory.getAbsolutePath();
            (new create()).createFolder(systemPath, "\\serverFile");
            systemPath += "\\serverFile";
            String srcPath = null;
            //获取Socket的输出流，用来向客户端发送数据
            PrintStream out = new PrintStream(client.getOutputStream());
            //获取Socket的输入流，用来接收从客户端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean isLogout = false;
            while (!isLogout) {
                //接收从客户端发送过来的数据
                str = buf.readLine();
                switch (str){
                    case "logout":{
                        //out.println("logout");
                        isLogout = true;
                        break;
                    }
                    case "refresh":{
                        refresh(out, systemPath);
                        break;
                    }
                    case "back":{
                        //获取当前父文件夹
                        String dir = (new getInformation()).getDirName(systemPath);
                        if (systemPath.length() > 3){
                            systemPath = systemPath.substring(0,systemPath.length()-dir.length()-1);
                        }
                        if (systemPath.length() < 3){
                            systemPath += "\\";
                        }
                        refresh(out,systemPath);
                        out.println("当前路径为：" + systemPath);
                        break;
                    }
                    case "createFolder":{
                        String name = buf.readLine();
                        String temp = (new create()).createFolder(systemPath + "\\",name);
                        refresh(out, systemPath);
                        out.println(temp);
                        break;
                    }
                    case "createFile":{
                        String name = buf.readLine();
                        String temp = (new create()).createFile(systemPath + "\\", name);
                        refresh(out, systemPath);
                        out.println(temp);
                        break;
                    }
                    case "getin":{
                        String folderName = buf.readLine();
                        ArrayList<String> files = (new getInformation()).getFiles(systemPath);
                        if (files.contains(folderName)){
                            systemPath += "\\" + folderName;
                            out.println("已进入文件夹 " +  systemPath);
                        }
                        else {
                            out.println("文件夹 " + folderName + " 不存在。");
                        }
                        refresh(out,systemPath);
                        break;
                    }
                    case "copy":{
                        String name = buf.readLine();
                        ArrayList<String> files = (new getInformation()).getFiles(systemPath);
                        if (files.contains(name)){
                            srcPath = systemPath + "\\" + name;
                            out.println("已拷贝 " + name);
                        }
                        else {
                            out.println("文件 " + name + " 不存在。");
                        }
                        break;
                    }
                    case "paste":{
                        if (srcPath != null){
                            (new copy()).copyGeneralFile(srcPath, systemPath);
                            out.println("已拷贝 " + srcPath);
                            refresh(out, systemPath);
                        }
                        else{
                            out.println("未拷贝源文件。");
                        }
                        break;
                    }
                    case "delete":{
                        ArrayList<String> temp = new ArrayList<String>();
                        String name = buf.readLine();
                        File delFile = new File(systemPath + "\\" + name);
                        delete delete = new delete();
                        if (delFile.isDirectory()){
                            temp  = delete.deleteFolder(systemPath + "\\", name);
                            for (String t : temp){
                                out.println(t);
                            }
                        }
                        else {
                            temp = delete.deleteFile(systemPath + "\\", name);
                            for (String t : temp){
                                out.println(t);
                            }
                        }
                        refresh(out,systemPath);
                        break;
                    }
                    case "encrypt":{
                        String name = buf.readLine();
                        String password = buf.readLine();
                        //如果不存在该文件
                        if (!(new getInformation()).getFiles(systemPath).contains(name)){
                            out.println("文件 " + name + " 不存在。");
                            break;
                        }
                        File file = new File(systemPath + "\\" + name);
                        if (!file.isDirectory()){
                            (new crypto()).Enc(password, systemPath, name);
                            out.println("已对 " + name + " 加密。");
                        }
                        else {
                            out.println("加密失败，不能加密文件夹。");
                        }
                        break;
                    }
                    case "decrypt":{
                        String name = buf.readLine();
                        String password = buf.readLine();
                        //如果不存在该文件
                        if (!(new getInformation()).getFiles(systemPath).contains(name)){
                            out.println("文件 " + name + " 不存在。");
                            break;
                        }
                        File file = new File(systemPath + "\\" + name);
                        if (!file.isDirectory()){
                            (new crypto()).Dec(password, systemPath, name);
                            out.println("已对 " + name + " 解密。");
                        }
                        else {
                            out.println("解密失败，不能解密文件夹。");
                        }
                        break;
                    }
                    case "upload":{
                        @SuppressWarnings("resource")
                        PrintWriter temppw = new PrintWriter(new FileWriter(systemPath), true);
                        String templine = null;
                        while ((templine = buf.readLine()) != null) {
                            if(templine.equals("#@#@Over@#@#")) {
                                break;
                            }
                            temppw.println(templine);
                        }
                        out.println("上传完成。");
                        break;
                    }
                    case "download":{
                        String name = buf.readLine();
                        @SuppressWarnings("resource")
                        BufferedReader tempbf = new BufferedReader(new FileReader(systemPath + "\\" + name));
                        OutputStream tempout = client.getOutputStream();
                        PrintWriter temppw = new PrintWriter(tempout, true);
                        String tempString = null;
                        while ((tempString = tempbf.readLine()) != null) {
                            temppw.println(tempString);
                        }
                        temppw.println("#@#@Over@#@#");
                        out.println("下载完成。");
                        break;
                    }
                    default:
                        break;
                }

            }
            out.close();
            client.close();
            System.out.println("客户端连接关闭");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}