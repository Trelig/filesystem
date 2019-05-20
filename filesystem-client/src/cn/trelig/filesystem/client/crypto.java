package cn.trelig.filesystem.client;

import java.io.*;
import java.security.*;
import javax.crypto.*;

public class crypto {
    private Key key;
    String path;
    //文件加密
    public void Enc(String password,String path_File, String name_File)
    {
        path = path_File;
        delete d = new delete();
        createKey(password);//生成密匙
        File sourcefile=new File(path_File+"\\"+name_File);
        try
        {
            File tempFile = Encrypt(sourcefile);
            d.deleteFile(path_File+"\\",name_File);
            tempFile.renameTo(sourcefile);
            //System.out.println("文件加密成功！");
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    //通过用户输入的密码来生成密钥
    private void createKey(String password)
    {
        try
        {
            KeyGenerator _generator = KeyGenerator.getInstance("DES"); //构造KeyGenerator对象
            _generator.init(new SecureRandom(password.getBytes()));  //通过随机方式初始化KeyGenerator对象
            this.key = _generator.generateKey();//密钥生成
            _generator = null;
        }
        catch (Exception e)
        {
            throw new RuntimeException( e);
        }
    }
    //加密函数
    private File Encrypt(File file) throws Exception
    {
        String temp_path = path;
        String temp_name = "tempfile.txt";
        File tempFile = new File(temp_path+"\\"+temp_name);
        Cipher cipher = Cipher.getInstance("DES");//构造Cipher对象，并说明加密算法是Des
        cipher.init(Cipher.ENCRYPT_MODE, this.key);//初始化Cipher对象,操作模式为加密，key为密钥
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(tempFile);
        CipherInputStream cis = new CipherInputStream(is, cipher);
        byte[] buffer = new byte[1024];
        int temp_char;
        while ((temp_char = cis.read(buffer)) > 0)
        {
            out.write(buffer, 0, temp_char);
        }
        cis.close();
        is.close();
        out.close();
        return tempFile;
    }

    //解密
    private File Decrypt(File file) throws Exception
    {
        String temp_path1 = path;
        String temp_name1 = "tempfile.txt";
        File tempFile = new File(temp_path1+"\\"+temp_name1);
        Cipher cipher = Cipher.getInstance("DES");//构造Cipher对象，并说明加密算法是Des
        cipher.init(Cipher.DECRYPT_MODE, this.key);//初始化Cipher对象,操作模式为解密，key为密钥
        InputStream is = new FileInputStream(file);
        OutputStream out = new FileOutputStream(tempFile);
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        byte[] buffer = new byte[1024];
        int r;
        while ((r = is.read(buffer)) >= 0)
        {
            System.out.println();
            cos.write(buffer, 0, r);
        }
        cos.close();
        out.close();
        is.close();
        return tempFile;
    }
    public void Dec(String password,String path_File, String name_File)
    {
        path = path_File;
        delete d = new delete();
        createKey(password);//生成密匙
        File sourcefile=new File(path_File+"\\"+name_File);
        try{
            File tempFile = Decrypt(sourcefile);
            d.deleteFile(path_File+"\\",name_File);
            tempFile.renameTo(sourcefile);
            // System.out.println("文件解密成功！");
        }
        catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
