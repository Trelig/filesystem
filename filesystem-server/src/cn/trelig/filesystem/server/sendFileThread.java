package cn.trelig.filesystem.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;

public class sendFileThread implements Runnable{
    private FileInputStream fileInput;
    private DataOutputStream dataOutput;
    private Socket fileClient;
    public final static int bytesize = 2048;    //文件传输时的字大小
    String fileName;
    String ip;

    public sendFileThread(String fileName, String ip){
        this.fileName = fileName;
        this.ip = ip;
    }
    @Override
    public void run() {
        try {
            File file = new File(fileName);
            fileClient = new Socket(ip,8899);
            fileInput = new FileInputStream(file);
            dataOutput = new DataOutputStream(fileClient.getOutputStream());
            System.out.println("开始预处理要发送的文件...");
            //发送文件名和长度
            dataOutput.writeUTF(file.getName());
            dataOutput.flush();
            dataOutput.writeLong(file.length());
            dataOutput.flush();

            System.out.println("开始传输文件");
            byte[] bytes = new byte[bytesize];
            int length = 0;
            long progress = 0;
            while ((length = fileInput.read(bytes,0,bytes.length)) != -1){
                dataOutput.write(bytes,0,length);
                dataOutput.flush();
                progress += length;
                System.out.println("已传输| " + (100*progress/file.length()) + "% |");
            }
            fileInput.close();
            dataOutput.close();
            fileClient.close();
            System.out.println("文件传输结束");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
