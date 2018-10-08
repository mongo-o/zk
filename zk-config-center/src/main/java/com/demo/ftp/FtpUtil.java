package com.demo.ftp;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.SocketException;

/**
 * @author AYL    2018/10/8 16:45
 */
public class FtpUtil {
    private static Logger logger = LogManager.getLogger();
    private static FTPClient ftpClient;

    /**
     * 连接ftp服务器
     * @param ftpConfig
     * @return
     * @throws IOException
     */
    private static boolean connectFtp(FtpConfig ftpConfig) throws IOException {
        ftpClient = new FTPClient();

        if (ftpConfig.getPort() == null) {
            ftpClient.connect(ftpConfig.getIpAddr());
        } else {
            ftpClient.connect(ftpConfig.getIpAddr(), ftpConfig.getPort());
        }
        ftpClient.login(ftpConfig.getUserName(), ftpConfig.getPassword());
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

        int reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftpClient.disconnect();
            return false;
        }
        ftpClient.changeWorkingDirectory(ftpConfig.getPath());
        return true;
    }

    /**
     * 关闭ftp连接
     */
    public static void closeFtp() {
        if (ftpClient != null && ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("=====关闭ftp连接失败：" + e.getMessage());
            }
        }
    }

    /**
     *
     * @param ftpConfig
     * @param fileName
     * @param serverPath ftp服务器的目标文件的全路径
     * @param localPath
     * @return
     * @throws IOException
     */
    public static boolean downloadFile(FtpConfig ftpConfig,String fileName,
                                       String serverPath, String localPath) throws IOException {
        return preDownloadFile(ftpConfig, fileName, serverPath, localPath);
    }

    /**
     *测试未通过。暂不可用
     *
     */
    public static boolean uploadFile(FtpConfig ftpConfig, File fileUpload) throws IOException {
        if (connectFtp(ftpConfig)) {
            if (fileUpload.isDirectory()) {
                //如果fileUpload是一个dir的话，getName（）返回的是路径的最后的文件夹名称
                if (ftpClient.makeDirectory(fileUpload.getName())) {
                    if (ftpClient.changeWorkingDirectory(fileUpload.getName())) {
                        String[] files = fileUpload.list();
                        for (String item : files) {
                            File file = new File(fileUpload.getPath() + File.separator + item);
                            if (file.isDirectory()) {
                                uploadFile(ftpConfig, file);
                                ftpClient.changeToParentDirectory();
                            } else {
                                File targetFile = new File(fileUpload.getPath() + File.separator + item);
                                return doUploadFile(targetFile);
                            }
                        }
                    } else {
                        logger.error("ftp changeWorkingDirectory到路径：{}失败.", fileUpload.getName());
                    }
                } else {
                    logger.error("ftp 创建文件路径失败:{}", fileUpload.getName());
                }
            } else {
                return doUploadFile(fileUpload);
            }
        } else {
            logger.error("连接ftp失败");
        }
        return false;
    }

    private static boolean doUploadFile(File fileToUpload) {
        try (FileInputStream fileInputStream = new FileInputStream(fileToUpload)) {
            return ftpClient.storeFile(fileToUpload.getName(), fileInputStream);
        } catch (IOException e) {
            logger.error("=========上传文件{}失败，原因:{}", fileToUpload.getPath() + File.separator  + fileToUpload.getName(),
                    e.getMessage());
            return false;
        }
    }

    private static boolean preDownloadFile(FtpConfig ftpConfig,String fileName,
                                           String serverPath, String localPath) throws IOException {
        if (connectFtp(ftpConfig)) {
            boolean changeWorkingDir = ftpClient.changeWorkingDirectory(serverPath);
            if (changeWorkingDir) {
                FTPFile[] ftpFiles = ftpClient.listFiles();
                for(FTPFile ftpFile : ftpFiles) {
                    if (ftpFile.getName().equals(fileName)) {
                        return doDownLoadFile(ftpFile, localPath);
                    }
                }
            } else {
                logger.error("切换ftp工作目录至{}失败", serverPath);
            }
        } else {
            logger.error("连接ftp服务器失败");
        }
        return false;
    }

    private static boolean doDownLoadFile(FTPFile ftpFile, String localPath) {
        if (ftpFile.isFile()) {
            OutputStream outputStream = null;

            try {
                File entryDir = new File(localPath);
                if (!entryDir.exists() || !entryDir.isDirectory()) {
                    entryDir.mkdirs();
                }
                String localFilePathName = localPath + File.separator + ftpFile.getName();
                File localFile = new File(localFilePathName);
                if (localFile.exists()) {
                    localFile.delete();
                }
                outputStream = new FileOutputStream(localFilePathName);
                ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                outputStream.flush();
                outputStream.close();
                return true;
            } catch (Exception e) {
                logger.error("======下载文件{}时失败==============", ftpFile.getName());
            }
        } else {
            logger.error("目标不是一个文件。");
        }
        return false;
    }
}
