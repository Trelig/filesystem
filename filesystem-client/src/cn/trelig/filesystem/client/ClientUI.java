package cn.trelig.filesystem.client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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

    private Socket client = null;
    private PrintStream out;
    private BufferedReader buf;

    static String currentPath;
    static String srcPath = null;
    static boolean loginStatu = false;


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
        loginButton.setEnabled(!flag);
    }

    void print(ArrayList<String> out){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");

        for (String temp : out){
            output.append(df.format(new Date()) + ": " + temp + "\n");
        }
    }

    public void refresh(){
        getInformation getInformation = new getInformation();
        ArrayList<String> files = getInformation.getFiles(currentPath);
        //System.out.println(files);
        clientList.setText("");
        for(String file : files){
            clientList.append(file + "\n");
        }
    }

    public void serverShow(BufferedReader buf){
        serverList.setText("");
        try{
            for (int i = 0; i < buf.read(); i++){
                serverList.append(buf.readLine() + "\n");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ClientUI() {

        clientRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                getInformation getInformation = new getInformation();
//                ArrayList<String> files = getInformation.getFiles(currentPath);
//                //System.out.println(files);
//                clientList.setText("");
//                for(String file : files){
//                    clientList.append(file + "\n");
//                }
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
                if (currentPath.length() < 3){
                    currentPath += "\\";
                }
                ArrayList<String> temp = new ArrayList<String>();
                temp.add("已返回上一层");
                temp.add("当前路径为：" + currentPath);
                print(temp);
                refresh();
            }
        });
        clientCreateFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                String folderName = JOptionPane.showInputDialog("请输入创建的文件夹名:");
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
        clientCreateFileButton.addActionListener(new ActionListener() {
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
        clientCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = "";
                String name = JOptionPane.showInputDialog("请输入要拷贝的文件/文件夹名:");
                if (name != null){
                    getInformation getInformation = new getInformation();
                    ArrayList<String> files = getInformation.getFiles(currentPath);
                    if (files.contains(name)){
                        srcPath = currentPath + "\\" + name;
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
        clientPasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                if (srcPath != null){
                    copy copy = new copy();
                    temp = copy.copyGeneralFile(srcPath, currentPath + "\\");
                    srcPath = null;
                }
                else {
                    temp.add("未拷贝源文件/文件夹。");
                }
                print(temp);
                refresh();
            }
        });
        clientDeleteButton.addActionListener(new ActionListener() {
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
        clientEncryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> temp = new ArrayList<String>();
                String name = JOptionPane.showInputDialog("请输入要加密的文件名:");
                if (name != null ){
                    File file = new File(currentPath + "\\" + name);
                    if (!file.isDirectory()){
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
        clientDecryptButton.addActionListener(new ActionListener() {
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
                        crypto.Dec(password, currentPath, name);
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
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<String> something = new ArrayList<String>();
                String temp = null;
                String ip = IPtext.getText();
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
                    String checkstatus = buf.readLine();
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
        logoutButton.addActionListener(new ActionListener() {
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
                }
                catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        serverRefreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    out.println("refresh");
                    if (buf.readLine() == "refresh"){
                        serverShow(buf);
                    }
                }catch (Exception e1){
                    e1.printStackTrace();
                }

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
        create.createFolder(currentPath,"\\ClientFile");
        currentPath += "\\ClientFile";
        //System.out.println(currentPath);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        output = new JTextArea();
        Scroll = new JScrollPane(output);

    }
}
