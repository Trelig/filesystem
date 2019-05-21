package cn.trelig.filesystem.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class recieveFileThread implements Runnable{
    private DataInputStream dataInput;
    private FileOutputStream fileOutput;
    private ServerSocket fileServer;
    private Socket fileClient = null;
    public final static int bytesize = 2048;    //文件传输时的字大小
    String path;
    PrintStream out;


    public recieveFileThread(String path,PrintStream out){
        this.path = path;
        this.out = out;
    }

    @Override
    public void run() {
        try{
            fileServer = new ServerSocket(8899);
            fileClient = fileServer.accept();
            dataInput = new DataInputStream(fileClient.getInputStream());
            // 文件名和长度
            String fileName = dataInput.readUTF();
            long fileLength = dataInput.readLong();
            File file = new File(path + "\\" + fileName);
            fileOutput = new FileOutputStream(file);

            byte[] bytes = new byte[bytesize];
            int length = 0;
            while ((length = dataInput.read(bytes,0,bytes.length)) != -1){
                fileOutput.write(bytes,0,length);
                fileOutput.flush();
            }
            System.out.println("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + fileLength + "] ========");
            fileOutput.close();
            dataInput.close();
            fileServer.close();
            fileClient.close();
            out.println("uploadOK");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
