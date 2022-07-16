package com.atguigu.gulimall.ware.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Author: 钟质昌
 * @Description: TODO
 * @DateTime: 2022-05-19 16:09
 **/
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("start insert field....");
        this.setFieldValByName("createTime", new Date(),metaObject);
        this.setFieldValByName("updateTime", new Date(),metaObject);
    }
    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", new Date(),metaObject);
    }
}
