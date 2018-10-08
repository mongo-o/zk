package com.demo.zkconfigcenter;

import com.demo.ftp.FtpConfig;
import com.demo.ftp.FtpUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author AYL    2018/10/8 20:06
 */
public class FtpUtilTest {
    private FtpConfig ftpConfig;

    @Before
    public void initFtpConfig(){
        FtpConfig ftpConfig = new FtpConfig();
        ftpConfig.setIpAddr("192.168.11.89");
        ftpConfig.setUserName("ftp_user");
        ftpConfig.setPassword("123456");
        ftpConfig.setPath("/home/" + ftpConfig.getUserName());
        this.ftpConfig = ftpConfig;
    }

    @Test
    public void downloadFileTest() throws IOException {

        String localPath = "D:\\git\\demo\\zk\\redisconfg";

        Assert.assertTrue(FtpUtil.downloadFile(ftpConfig, "ftp_root_dir_test",
                ftpConfig.getPath(), localPath));
        FtpUtil.closeFtp();
    }

    @Test
    public void uploadFileTest() throws IOException {
        String fileStr = "D:\\git\\demo\\zk\\redisconfg\\upload";
        File file = new File(fileStr);
        Assert.assertTrue(FtpUtil.uploadFile(ftpConfig, file));
    }
}
