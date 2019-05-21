package cn.trelig.filesystem.client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class uploadThread implements Runnable {
    private FileInputStream fileInput;
    private DataOutputStream dataOutput;
    private Socket fileClient;
    public final static int bytesize = 2048;    //文件传输时的字大小
    String ip;
    File file;
    JTextArea output;
    BufferedReader buf;      //从服务端接收消息的输入流

    public uploadThread(String ip, File file, JTextArea output,BufferedReader buf){
        this.file = file;
        this.ip = ip;
        this.output = output;
        this.buf = buf;
    }

    @Override
    public void run() {
        try {
            String temp = null;
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//获取当前时间，在输出语句前加上时间戳
            fileClient = new Socket(ip,8899);//一个socket只能有一个传输流，因此传输文件需要新建一个socket，避免传输结束后关闭了消息传输流
            fileInput = new FileInputStream(file);
            dataOutput = new DataOutputStream(fileClient.getOutputStream());

            temp = ("开始预处理要发送的文件...");
            output.append(df.format(new Date()) + ": " + temp + "\n");
            //发送文件名和长度
            dataOutput.writeUTF(file.getName());
            dataOutput.flush();
            dataOutput.writeLong(file.length());
            dataOutput.flush();

            temp = ("开始传输文件");
            output.append(df.format(new Date()) + ": " + temp + "\n");
            byte[] bytes = new byte[bytesize];
            int length = 0;
            long progress = 0;
            while ((length = fileInput.read(bytes,0,bytes.length)) != -1){
                dataOutput.write(bytes,0,length);
                dataOutput.flush();
                progress += length;
                temp = (file.getName() + "已上传| " + (100*progress/file.length()) + "% |");
                output.append(df.format(new Date()) + ": " + temp + "\n");
            }
            fileInput.close();
            dataOutput.close();
            fileClient.close();         //文件传输结束后关闭文件传输socket和传输流
            if (buf.readLine().equals("uploadOK")){ //与服务器确认是否上传成功
                temp = ("[Server]:文件上传成功！");
            }
            else {
                temp = ("[Server]:文件上传失败！");
            }
            output.append(df.format(new Date()) + ": " + temp + "\n");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
