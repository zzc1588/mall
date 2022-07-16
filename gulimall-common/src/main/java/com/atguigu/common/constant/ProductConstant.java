package com.atguigu.common.constant;

import lombok.Data;

public class ProductConstant {


    public enum  AttrEnum{
        ATTR_TYPE_BASE("BASE",1,"基本属性"),ATTR_TYPE_SALE("SALE",0,"销售属性");
        private String name;
        private int code;
        private String msg;

        AttrEnum(String name, int code, String msg) {
            this.name = name;
            this.code = code;
            this.msg = msg;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum  StatusEnum{
        NEW_SPU(0,"新建"),
        SPU_UP(1,"商品上架"),
        SPU_DOWN(2,"商品下架");
        private String name;
        private int code;
        private String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public String getName() {
            return name;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
