package com.demo.zkconfigcenter.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author AYL    2018/10/5 11:44
 */
@Getter
@Setter
@ToString(callSuper = true)
public class RedisConfig implements Serializable {

    private static final long serialVersionUID = -4467763921701072091L;
    /**
     * 类型可选项:
     * add
     * update
     * delete
     * stop:停止当前自动配置程序
     */
    private String type;

    /**
     * 新配置文件的获取地址
     */
    private String url;

    /**
     * 备注
     */
    private String remark;
}
