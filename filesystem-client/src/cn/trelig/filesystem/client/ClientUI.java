package cn.trelig.filesystem.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ClientUI extends JFrame{
    private JPanel contentPane;
    private JTextArea output;
    private JScrollPane Scroll;
    private JTextField IPtext;
    private JTextField userNameText;
    private JPasswordField password;
    private JTextField loginStatusText;
    private JButton loginButton;
    private JTextArea clientList;
    private JTextArea serverList;
    private JButton clientBackButton;
    private JButton clientRefreshButton;
    private JButton serverBackButton;
    private JButton serverRefreshButton;
    private JButton clientCreateFolderButton;
    private JButton clientDeleteButton;
    private JButton clientCreateFileButton;
    private JButton clientPasteButton;
    private JButton uploadButton;
    private JButton clientCopyButton;
    private JButton serverCreateFolderButton;
    private JButton serverDeleteButton;
    private JButton serverCreateFileButton;
    private JButton serverEncryptButton;
    private JButton serverDecryptButton;
    private JButton clientEncryptButton;
    private JButton clientDecryptButton;
    private JButton downloadButton;
    private JButton serverCopyButton;
    private JButton serverPasteButton;
    private JPanel serverButtonPane;
    private JPanel clientButtonPane;
    private JButton clientGetinButton;
    private JButton serverGetinButton;
    private JButton logoutButton;

    private Socket client = null;    //用于通信的socket
    private PrintStream out;         //往服务端发消息的输出流
    private BufferedReader buf;      //从服务端接收消息的输入流

    static String currentPath;      //本地的当前路径
    static String srcPath = null;   //拷贝使用的源文件路径
    static boolean loginStatu = false;  //检验是否登录
    public final static int bytesize = 2048;    //文件传输时的字大小
    static String ip;


    //设置未登录时和服务端相关的操作按钮不可点击
    void changeButton(boolean flag){
        logoutButton.setEnabled(flag);
        serverRefreshButton.setEnabled(flag);
        serverBackButton.setEnabled(flag);
        serverCreateFolderButton.setEnabled(flag);
        serverCreateFileButton.setEnabled(flag);
        serverGetinButton.setEnabled(flag);
        serverCopyButton.setEnabled(flag);
        serverPasteButton.setEnabled(flag);
        serverDeleteButton.setEnabled(flag);
        serverEncryptButton.setEnabled(flag);
        serverEncryptButton.setEnabled(flag);
        serverDecryptButton.setEnabled(flag);
        uploadButton.setEnabled(flag);
        downloadButton.setEnabled(flag);
        loginButton.setEnabled(!flag);      //登录后不可再次登录
    }

    //用于往输出框内输出内容
    void print(ArrayList<String> out){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//获取当前时间，在输出语句前加上时间戳
        for (String temp : out){
            output.append(df.format(new Date()) + ": " + temp + "\n");
        }
    }

    //本地的刷新文件列表功能函数，同时往客户端显示框内输出当前路径下的所有文件
    public void refresh(){
        getInformation getInformation = new getInformation();
        ArrayList<String> files = getInformation.getFiles(currentPath);
        //System.out.println(files);
        clientList.setText("");
        for(String file : files){
            clientList.append(file + "\n");
        }
    }

    //服务器的刷新功能函数
    public void serverRefresh(){
        serverList.setText("");
        try{
            out.println("refresh");         //往服务器发送刷新指令
            if (buf.readLine().equals("refresh")){      //处理返回的文件列表
                //System.out.println("test");
                int size = Integer.parseInt(buf.readLine());    //先获取返回的文件列表中的数量，避免读取服务器消息出错，再依次添加到显示框中
                //System.out.println(size);
                for (int i = 0; i < size; i++){
                    serverList.append(buf.readLine() + "\n");
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ClientUI() {

        clientRefreshButton.addActionListener(new ActionListener() {//刷新按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();

            }
        });
        clientBackButton.addActionListener(new ActionListener() {//返回上一层
            @Override
            public void actionPerformed(ActionEvent e) {
                getInformation getInformation = new getInformation();
                //获取当前父文件夹
                String dir = getInformation.getDirName(currentPath);
//                System.out.println(dir);
                if (currentPath.length() > 3){
                    currentPath = currentPath.substring(0,currentPath.length() - dir.length()-1);
                }
                if (currentPath.length() < 3){//设定最短的路径，避免多次返回截取当前路径出错
                    currentPath += "\\";
                }
                ArrayList<String> temp = new ArrayList<String>();
                temp.add("已返回上一层");
                temp.add("当前路径为：" + currentPath);
                print(temp);
                refresh();
            }
        });
        clientCreateFolderButton.addActionListener(new ActionListener() {//创建文件夹按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                String folderName = JOptionPane.showInputDialog("请输入创建的文件夹名:"); //java自带功能实现弹窗获取字符串
                if (folderName != null){
                    create create = new create();
                    temp = create.createFolder(currentPath + "\\", folderName);
                    something.add(temp);
                }
                else {
                    something.add("未创建文件夹。");
                }
                print(something);
                refresh();
            }
        });
        clientCreateFileButton.addActionListener(new ActionListener() {//创建文件announce
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                String fileName = JOptionPane.showInputDialog("请输入创建的文件名:");
                if (fileName != null){
                    create create = new create();
                    temp = create.createFile(currentPath + "\\", fileName);
                    something.add(temp);
                }
                else {
                    something.add("未创建文件。");
                }
                print(something);
                refresh();
            }
        });
        clientGetinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = "";
                String folderName = JOptionPane.showInputDialog("请输入要进入的文件夹名:");
                if (folderName != null){
                    getInformation getInformation = new getInformation();
                    ArrayList<String> files = getInformation.getFiles(currentPath);
                    if (files.contains(folderName)){
                        currentPath += "\\" + folderName;
                        temp = "已进入文件夹 " +  currentPath;
                    }
                    else {
                        temp = "文件夹 " + folderName + " 不存在。" ;
                    }
                    something.add(temp);
                }
                else {
                    temp = "未输入文件夹名。";
                    something.add(temp);
                }
                print(something);
                refresh();
            }
        });
        clientCopyButton.addActionListener(new ActionListener() {//拷贝按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = "";
                String name = JOptionPane.showInputDialog("请输入要拷贝的文件/文件夹名:");
                if (name != null){
                    getInformation getInformation = new getInformation();
                    ArrayList<String> files = getInformation.getFiles(currentPath);
                    if (files.contains(name)){
                        srcPath = currentPath + "\\" + name;        //拷贝操作时只记录需要拷贝的路径，只有粘贴时才有对文件的操作
                        temp = "文件 " + name + " 已拷贝。";
                    }
                    else {
                        temp = "文件 " + name + " 不存在。";
                    }
                    something.add(temp);
                }
                else {
                    temp = "未输入文件/文件夹名。";
                    something.add(temp);
                }
                print(something);
            }
        });
        clientPasteButton.addActionListener(new ActionListener() {//粘贴按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                if (srcPath != null){
                    copy copy = new copy();
                    temp = copy.copyGeneralFile(srcPath, currentPath + "\\");//将拷贝记录的源文件路径进行文件/文件夹拷贝操作
                    srcPath = null;
                }
                else {
                    temp.add("未拷贝源文件/文件夹。");
                }
                print(temp);
                refresh();
            }
        });
        clientDeleteButton.addActionListener(new ActionListener() {//删除按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要删除的文件/文件夹名:");
                if (name != null){
                    File delFile = new File(currentPath + "\\" + name);
                    delete delete = new delete();
                    if(delFile.isDirectory()){
                        temp  = delete.deleteFolder(currentPath + "\\", name);
                    }
                    else {
                        temp = delete.deleteFile(currentPath + "\\", name);
                    }
                }
                else {
                    temp.add("未输入文件/文件夹名。");
                }
                print(temp);
                refresh();
            }
        });
        clientEncryptButton.addActionListener(new ActionListener() {//加密按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要加密的文件名:");
                if (name != null ){
                    File file = new File(currentPath + "\\" + name);
                    if (!file.isDirectory()){
                        //弹出密码框获取加密密码
                        JPasswordField pw = new JPasswordField();
                        JOptionPane.showMessageDialog(null, pw, "请输入加密的密码", JOptionPane.PLAIN_MESSAGE);
                        String password = String.valueOf(pw.getPassword());
                        crypto crypto = new crypto();
                        crypto.Enc(password, currentPath, name);
                        temp.add("已对 " + name + " 加密。");
                    }
                    else {
                        temp.add("加密失败，不能加密文件夹。");
                    }
                }
                else {
                    temp.add("未输入文件名。");
                }
                print(temp);
                refresh();
            }
        });
        clientDecryptButton.addActionListener(new ActionListener() {//解密按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要解密的文件名:");
                if (name != null ){
                    File file = new File(currentPath + "\\" + name);
                    if (!file.isDirectory()){
                        JPasswordField pw = new JPasswordField();
                        JOptionPane.showMessageDialog(null, pw, "请输入解密的密码", JOptionPane.PLAIN_MESSAGE);
                        String password = String.valueOf(pw.getPassword());
                        crypto crypto = new crypto();
                        crypto.Dec(password, currentPath, name);//缺陷就是，没有判断输入的密码是否正确，因此输入错误密码会损坏源文件
                        temp.add("已对 " + name + " 解密。");
                    }
                    else {
                        temp.add("解密失败，不能解密文件夹。");
                    }
                }
                else {
                    temp.add("未输入文件名。");
                }
                print(temp);
                refresh();
            }
        });
        loginButton.addActionListener(new ActionListener() {//登录按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                ip = IPtext.getText();
                String username = userNameText.getText();
                String passwd = String.valueOf(password.getPassword());
                try{
                    client = new Socket(ip, 10086);
                    client.setSoTimeout(10000);
                    //获取Socket的输出流，用来发送数据到服务端
                    out = new PrintStream(client.getOutputStream());
                    //获取Socket的输入流，用来接收从服务端发送过来的数据
                    buf =  new BufferedReader(new InputStreamReader(client.getInputStream()));
                    //发送用户名密码
                    out.println(username);
                    out.println(passwd);
                    String checkstatus = buf.readLine();//询问服务器输入的用户名和密码是否正确，只有登录成功了才能进行其他操作
                    if (checkstatus.equals("OK") ){
                        loginStatu = true;
                        changeButton(true);
                        temp = "登录成功。";
                        loginStatusText.setText("已登录");
                        loginStatusText.setForeground(Color.GREEN);
                    }
                    else if (checkstatus.equals("bad")){
                        temp = "用户名/密码错误，登录失败。";
                        loginStatusText.setText("登录失败");
                    }
                    something.add(temp);
                    print(something);
                }
                catch (Exception e1){
                    temp = ("连接失败。");
                    something.add(temp);
                    print(something);
                }

            }
        });
        logoutButton.addActionListener(new ActionListener() {//注销按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                try{
                    out.println("logout");
                    buf.close();
                    out.close();
                    client.close();
                    something.add("已注销，断开连接。");
                    loginStatusText.setText("已注销");
                    loginStatusText.setForeground(Color.RED);
                    changeButton(false);
                    print(something);
                    serverList.setText(""); //注销之后，不能再控制服务器，也不能显示服务器文件
                }
                catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        serverRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                serverRefresh();
                ArrayList<String> something = new ArrayList<String>();
                something.add("[Server]:已刷新");
                print(something);
            }
        });
        serverBackButton.addActionListener(new ActionListener() {//服务器返回上一层按钮
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                try{
                    out.println("back");
                    something.add("[Server]:" + buf.readLine());
                    serverRefresh();
                    print(something);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        serverCreateFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String folderName = JOptionPane.showInputDialog("请输入在服务器主机创建的文件夹名:");
                if (folderName != null){
                    try{
                        out.println("createFolder");
                        out.println(folderName);
                        something.add("[Server]:" + buf.readLine());
                        serverRefresh();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件夹名。");
                }
                print(something);
            }
        });
        serverCreateFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String fileName = JOptionPane.showInputDialog("请输入在服务器主机创建的文件名:");
                if (fileName != null){
                    try{
                        out.println("createFile");
                        out.println(fileName);
                        something.add("[Server]:" + buf.readLine());
                        serverRefresh();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件名。");
                }
                print(something);
            }
        });
        serverGetinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String folderName = JOptionPane.showInputDialog("请输入要进入的服务器主机文件夹名:");
                if (folderName != null){
                    try{
                        out.println("getin");
                        out.println(folderName);
                        something.add("[Server]:" + buf.readLine());
                        serverRefresh();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件夹名。");
                }
                print(something);
            }
        });
        serverCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要拷贝的服务器主机文件/文件夹名:");
                if (name != null){
                    try{
                        out.println("copy");
                        out.println(name);
                        something.add("[Server]:" + buf.readLine());
                        print(something);
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件/文件夹名。");
                }
                print(something);
            }
        });
        serverPasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                try{
                    out.println("paste");
                    something.add("[Server]:" + buf.readLine());
                    print(something);
                    serverRefresh();
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        serverDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要删除的服务器主机文件/文件夹名:");
                if (name != null){
                    try{
                        out.println("delete");
                        out.println(name);
                        int size = Integer.parseInt(buf.readLine());
                        for (int i = 0; i < size; i++){
                            something.add("[Server]:" + buf.readLine());
                        }
                        serverRefresh();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件/文件夹名。");
                }
                print(something);
            }
        });
        serverEncryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要加密的服务器主机文件名:");
                JPasswordField pw = new JPasswordField();
                JOptionPane.showMessageDialog(null, pw, "请输入加密的密码", JOptionPane.PLAIN_MESSAGE);
                String password = String.valueOf(pw.getPassword());
                if (name != null ){
                    try{
                        out.println("encrypt");
                        out.println(name);
                        out.println(password);
                        something.add("[Server]:" + buf.readLine());

                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件名。");
                }
                print(something);
            }
        });
        serverDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要解密的服务器主机文件名:");
                JPasswordField pw = new JPasswordField();
                JOptionPane.showMessageDialog(null, pw, "请输入解密的密码", JOptionPane.PLAIN_MESSAGE);
                String password = String.valueOf(pw.getPassword());
                if (name != null ){
                    try{
                        out.println("decrypt");
                        out.println(name);
                        out.println(password);
                        something.add("[Server]:" + buf.readLine());
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                else {
                    something.add("未输入文件名。");
                }
                print(something);
            }
        });
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileInputStream fileInput;
                DataOutputStream dataOutput;
                Socket fileClient;
                ArrayList<String> something = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要上传的文件名:");
                try{
                    @SuppressWarnings("resource")
                    File file = new File(currentPath + "\\" + name);
                    if (file.exists() && !file.isDirectory()){
                        out.println("upload");
                        fileClient = new Socket(ip,8899);//一个socket只能有一个传输流，因此传输文件需要新建一个socket，避免传输结束后关闭了消息传输流
                        fileInput = new FileInputStream(file);
                        dataOutput = new DataOutputStream(fileClient.getOutputStream());

                        something.add("开始预处理要发送的文件...");
                        //发送文件名和长度
                        dataOutput.writeUTF(file.getName());
                        dataOutput.flush();
                        dataOutput.writeLong(file.length());
                        dataOutput.flush();

                        something.add("开始传输文件");
                        print(something);
                        something.clear();//为了及时显示传输状态
                        byte[] bytes = new byte[bytesize];
                        int length = 0;
                        long progress = 0;
                        while ((length = fileInput.read(bytes,0,bytes.length)) != -1){
                            dataOutput.write(bytes,0,length);
                            dataOutput.flush();
                            progress += length;
                            something.add("| " + (100*progress/file.length()) + "% |");
                            print(something);
                            something.clear();
                        }
                        fileInput.close();
                        dataOutput.close();
                        fileClient.close();         //文件传输结束后关闭文件传输socket和传输流
                        if (buf.readLine().equals("uploadOK")){ //与服务器确认是否上传成功
                            something.add("[Server]:文件上传成功！");
                            serverRefresh();
                        }
                        else {
                            something.add("[Server]:文件上传失败！");
                        }

                    }
                    else {
                        something.add("文件 " + name +" 不存在。");
                    }
                    print(something);
                }catch (Exception e1){
                    e1.printStackTrace();
                }

            }
        });
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                boolean isExist = true;     //判断下载的文件是否存在的标志
                String name = JOptionPane.showInputDialog("请输入要下载的文件名:");
                try{
                    out.println("download");
                    out.println(name);
                    if (buf.readLine().equals("notExist")){
                        isExist = false;
                        something.add("[Server]:服务器主机不存在文件 " + name);
                    }

                }catch (Exception e1){
                    e1.printStackTrace();
                }
                if (isExist){           //文件存在，则把当前客户端当做文件传输的服务端用于接收文件
                    @SuppressWarnings("resource")
                    DataInputStream dataInput;
                    FileOutputStream fileOutput;
                    ServerSocket fileServer;
                    Socket fileClient = null;
                    try{
                        fileServer = new ServerSocket(8899);
                        fileClient = fileServer.accept();
                        dataInput = new DataInputStream(fileClient.getInputStream());
                        // 文件名和长度
                        String fileName = dataInput.readUTF();
                        long fileLength = dataInput.readLong();
                        File file = new File(currentPath + "\\" + fileName);    //文件下载到当前路径
                        fileOutput = new FileOutputStream(file);

                        byte[] bytes = new byte[bytesize];
                        int length = 0;
                        while ((length = dataInput.read(bytes,0,bytes.length)) != -1){
                            fileOutput.write(bytes,0,length);
                            fileOutput.flush();
                        }
                        something.add("======== 文件接收成功 [File Name：" + fileName + "] [Size：" + fileLength + "] ========");
                        fileOutput.close();
                        dataInput.close();
                        fileServer.close();
                        fileClient.close();
                        refresh();
                    }catch (Exception e1){
                        e1.printStackTrace();
                    }
                }
                print(something);

            }
        });
    }

    public static void main(String[] args) throws IOException {
        JFrame frame = new JFrame("文件管理系统");
        frame.setContentPane(new ClientUI().contentPane);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        File directory = new File("");
        currentPath = directory.getCanonicalPath();
        create create = new create();
        create.createFolder(currentPath,"\\ClientFile");//程序开始时先创建客户端专属文件夹
        currentPath += "\\ClientFile";
        //System.out.println(currentPath);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        output = new JTextArea();
        Scroll = new JScrollPane(output);//对输出框进行声明并把输出框和滚动条绑定

    }
}
