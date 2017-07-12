package com.townmc.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class Ftp {
    public FTPClient ftpClient;
    protected static Log log = LogFactory.getLog(Ftp.class);

    public Ftp(){
        ftpClient = new FTPClient();
    }

    /**
     * 连接到FTP服务器
     * @param url 主机名
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @return 是否连接成功
     * @throws java.io.IOException
     */
    public boolean connect(String url, int port, String username, String password) throws IOException {

        ftpClient.connect(url, port);
        ftpClient.setControlEncoding("UTF-8");
        if(FTPReply.isPositiveCompletion(ftpClient.getReplyCode())){
            if(ftpClient.login(username, password)){
                ftpClient.enterLocalPassiveMode();
                //设置以二进制流的方式传输
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                return true;
            }
        }
        disconnect();
        return false;

    }

    /**
     * 获得远程ftp服务器下文件目录
     * @param path
     * @return
     * @throws java.io.IOException
     */
    public FTPFile[] listFiles(String path) throws IOException{
        return ftpClient.listFiles(path);
    }

    /** *//**
     * @param local 本地文件名称，绝对路径
     * @param remote 远程文件路径，使用/home/directory1/subdirectory/file.ext或是 http://www.guihua.org /subdirectory/file.ext
     * 按照Linux上的路径指定方式，支持多级目录嵌套，支持递归创建不存在的目录结构
     * @return 上传结果
     * @throws java.io.IOException
     */
    public boolean upload(String local,String remote) throws IOException{
        boolean result;
        //对远程目录的处理
        String remoteFileName = remote;
        if(remote.contains("/")){
            remoteFileName = remote.substring(remote.lastIndexOf("/")+1);
            //创建服务器远程目录结构，创建失败直接返回
            if(createDirecroty(remote, ftpClient)==0){
                result = false;
            }
        }
        //检查远程是否存在文件
        FTPFile[] files = ftpClient.listFiles(new String(remoteFileName.getBytes("GBK"),"utf-8"));
        if(files.length == 1){
            ftpClient.deleteFile(files[0].getName());
        }
        result = uploadFile(remoteFileName, new File(local));
        return result;
    }

    /**
     * 断开与远程服务器的连接
     */
    public void disconnect() throws IOException{
        if(ftpClient.isConnected()){
            ftpClient.disconnect();
        }
    }

    /**
     * 删除ftp服务器上的文件
     * @param pathname
     * @return 是否删除成功
     * @throws Exception
     */
    public boolean deleteFile(String pathname) throws Exception{
        return ftpClient.deleteFile(pathname);
    }

    /**
     * 递归创建远程服务器目录
     * @param remote 远程服务器文件绝对路径
     * @param ftpClient FTPClient 对象
     * @return 目录创建是否成功
     */
    public int createDirecroty(String remote, FTPClient ftpClient) throws IOException{
        int status = 1;
        String directory = remote.substring(0,remote.lastIndexOf("/")+1);
        if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"utf-8"))){
            //如果远程目录不存在，则递归创建远程服务器目录
            int start=0;
            int end = 0;
            if(directory.startsWith("/")){
                start = 1;
            }else{
                start = 0;
            }
            end = directory.indexOf("/",start);
            while(true){
                String subDirectory = new String(remote.substring(start,end).getBytes("GBK"),"utf-8");
                if(!ftpClient.changeWorkingDirectory(subDirectory)){
                    if(ftpClient.makeDirectory(subDirectory)){
                        ftpClient.changeWorkingDirectory(subDirectory);
                    }else {
                        log.debug("创建目录失败");
                        return 0;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/",start);
                //检查所有目录是否创建完毕
                if(end <= start){
                    break;
                }
            }
        }
        return status;
    }

    /**
     * @param remoteFile 远程文件名，在上传之前已经将服务器工作目录做了改变
     * @param localFile 本地文件 File句柄，绝对路径
     * @return boolean
     * @throws java.io.IOException
     */
    public boolean uploadFile(String remoteFile,File localFile) throws IOException{
        RandomAccessFile raf = new RandomAccessFile(localFile,"r");
        OutputStream out = this.ftpClient.appendFileStream(new String(remoteFile.getBytes("GBK"),"utf-8"));
        byte[] bytes = new byte[1024];
        int c;
        while((c = raf.read(bytes))!= -1){
            out.write(bytes,0,c);
        }
        out.flush();
        raf.close();
        out.close();
        boolean result =this.ftpClient.completePendingCommand();
        return result;
    }
}
