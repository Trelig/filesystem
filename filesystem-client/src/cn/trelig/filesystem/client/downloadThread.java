package cn.trelig.filesystem.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class downloadThread implements Runnable{
    private DataInputStream dataInput;
    private FileOutputStream fileOutput;
    private ServerSocket fileServer;
    private Socket fileClient = null;
    ArrayList<String> something = new ArrayList<String>();

    public downloadThread(String path){

    }

    @Override
    public void run() {

    }
}
