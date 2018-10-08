package com.demo.ftp;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author AYL    2018/10/8 16:44
 */
@Getter
@Setter
@ToString(callSuper = true)
public class FtpConfig {
    private String ipAddr;
    private Integer port;
    private String userName;
    private String password;
    private String path;
}
