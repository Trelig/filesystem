package cn.trelig.filesystem.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class server {

    public static void main(String[] args){
        String username = "trelig";
        String password = "123456";

        try {
            //服务端在10086端口监听客户端请求的TCP连接
            ServerSocket server = new ServerSocket(10086);
            Socket client = null;
            System.out.println("服务器已启动，正在监听10086端口...");
            while (true){
                //等待客户端的连接，如果没有获取连接
                client = server.accept();
                System.out.println("与客户端连接成功！");
                //获取Socket的输出流，用来向客户端发送数据
                PrintStream out = new PrintStream(client.getOutputStream());
                //获取Socket的输入流，用来接收从客户端发送过来的数据
                BufferedReader buf = new BufferedReader(new InputStreamReader(client.getInputStream()));
                if (username.equals(buf.readLine()) && password.equals(buf.readLine())){
                    //为每个客户端连接开启一个线程
                    out.println("OK");
                    System.out.println("客户端登录成功！");
                    new Thread(new serverThread(client)).start();
                }
                else{
                    out.println("bad");
                    System.out.println("客户端登录失败！");
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
