package cn.trelig.filesystem.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class downloadThread implements Runnable{
    private DataInputStream dataInput;
    private FileOutputStream fileOutput;
    private ServerSocket fileServer;
    private Socket fileClient = null;
    public final static int bytesize = 2048;    //文件传输时的字大小
    JTextArea output;
    String path;

    public downloadThread(String path, JTextArea output){
        this.output = output;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            String temp = null;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//获取当前时间，在输出语句前加上时间戳
            fileServer = new ServerSocket(8899);
            fileClient = fileServer.accept();
            dataInput = new DataInputStream(fileClient.getInputStream());
            // 文件名和长度
            String fileName = dataInput.readUTF();
            long fileLength = dataInput.readLong();
            File file = new File(path + "\\" + fileName);    //文件下载到当前路径
            fileOutput = new FileOutputStream(file);
            byte[] bytes = new byte[bytesize];
            int length = 0;
            long progress = 0;
            while ((length = dataInput.read(bytes,0,bytes.length)) != -1){
                fileOutput.write(bytes,0,length);
                fileOutput.flush();
                progress += length;
                temp = (fileName + "已下载| " + (100*progress/fileLength) + "% |");
                output.append(df.format(new Date()) + ": " + temp + "\n");
            }

            temp = ("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + fileLength + "] ========");
            output.append(df.format(new Date()) + ": " + temp + "\n");
            fileOutput.close();
            dataInput.close();
            fileServer.close();
            fileClient.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
