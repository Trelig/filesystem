package cn.trelig.filesystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class serverThread implements Runnable {//服务器线程类，用于处理客户端的交互
    public final static int bytesize = 2048;
    private Socket client = null;

    public serverThread(Socket client) {
        this.client = client;
    }

    public void refresh(PrintStream out, String systemPath){//服务器刷新功能，用于将所有文件信息逐条发送至客户端
        ArrayList<String> files = (new getInformation()).getFiles(systemPath);
        out.println("refresh");
        out.println(files.size());
        //System.out.println(files.size());
        for (String filename : files){
            out.println(filename);
            //System.out.println(filename);
        }
    }

    String str = null;
    @Override
    public void run() {
        try {
            File directory = new File("");//设定为当前文件夹
            String systemPath = directory.getAbsolutePath();
            (new create()).createFolder(systemPath, "\\serverFile");//程序运行时创建服务器专用文件夹
            systemPath += "\\serverFile";
            String srcPath = null;
            //获取Socket的输出流，用来向客户端发送数据
            PrintStream out = new PrintStream(client.getOutputStream());
            //获取Socket的输入流，用来接收从客户端发送过来的数据
            BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
            boolean isLogout = false;
            while (!isLogout) {
                //接收从客户端发送过来的数据
                str = buf.readLine();//先读取与客户端约定的指令
                switch (str){
                    case "logout":{
                        //out.println("logout");
                        isLogout = true;    //接收到注销指令，不再接收客户端消息
                        break;
                    }
                    case "refresh":{
                        System.out.println("收到refresh指令");
                        //System.out.println(systemPath);
                        refresh(out, systemPath);
                        break;
                    }
                    case "back":{
                        System.out.println("收到back指令");
                        //获取当前父文件夹
                        String dir = (new getInformation()).getDirName(systemPath);
                        if (systemPath.length() > 3){
                            systemPath = systemPath.substring(0,systemPath.length()-dir.length()-1);
                        }
                        if (systemPath.length() < 3){
                            systemPath += "\\";
                        }
//                        refresh(out,systemPath);
                        out.println("当前路径为：" + systemPath);
                        break;
                    }
                    case "createFolder":{
                        System.out.println("收到createFolder指令");
                        String name = buf.readLine();
                        String temp = (new create()).createFolder(systemPath + "\\",name);
//                        refresh(out, systemPath);
                        out.println(temp);
                        break;
                    }
                    case "createFile":{
                        System.out.println("收到createFile指令");
                        String name = buf.readLine();
                        String temp = (new create()).createFile(systemPath + "\\", name);
//                        refresh(out, systemPath);
                        out.println(temp);
                        break;
                    }
                    case "getin":{
                        System.out.println("收到getin指令");
                        String folderName = buf.readLine();
                        ArrayList<String> files = (new getInformation()).getFiles(systemPath);
                        if (files.contains(folderName)){
                            systemPath += "\\" + folderName;
                            out.println("已进入文件夹 " +  systemPath);
                        }
                        else {
                            out.println("文件夹 " + folderName + " 不存在。");
                        }
//                        refresh(out,systemPath);
                        break;
                    }
                    case "copy":{
                        System.out.println("收到copy指令");
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
                        System.out.println("收到paste指令");
                        if (srcPath != null){
                            (new copy()).copyGeneralFile(srcPath, systemPath);
                            out.println("已粘贴 " + srcPath);
                            srcPath = null;
//                            refresh(out, systemPath);
                        }
                        else{
                            out.println("未拷贝源文件。");
                        }
                        break;
                    }
                    case "delete":{
                        System.out.println("收到delete指令");
                        ArrayList<String> temp = new ArrayList<String>();
                        String name = buf.readLine();
                        File delFile = new File(systemPath + "\\" + name);
                        delete delete = new delete();
                        if (delFile.isDirectory()){
                            temp  = delete.deleteFolder(systemPath + "\\", name);
                            out.println(temp.size());
                            for (String t : temp){
                                out.println(t);
                            }
                        }
                        else {
                            temp = delete.deleteFile(systemPath + "\\", name);
                            out.println(temp.size());
                            for (String t : temp){
                                out.println(t);
                            }
                        }
//                        refresh(out,systemPath);
                        break;
                    }
                    case "encrypt":{
                        System.out.println("收到encrypt指令");
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
                        System.out.println("收到decrypt指令");
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
                        System.out.println("收到upload指令");
                        new Thread(new recieveFileThread(systemPath, out)).start();
                        break;
                    }
                    case "download":{
                        System.out.println("收到download指令");
                        String name = buf.readLine();
                        File tempfile = new File(systemPath + "\\" + name);
                        if (!tempfile.exists() || tempfile.isDirectory())//文件不存在或者选择的文件名是文件夹
                            out.println("notExist");
                        else{
                            out.println("exist");
                            new Thread(new sendFileThread(systemPath + "\\" + name, client.getLocalAddress().getHostAddress())).start();
                        }
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