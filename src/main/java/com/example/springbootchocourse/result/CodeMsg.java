package com.example.springbootchocourse.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeMsg {

    private int code;
    private String msg;

    //通用的错误码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常：%s");
    public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(500102, "访问高峰期，请稍等！");
    //登录模块5002**
    public static CodeMsg ID_NOT_EXIST = new CodeMsg(500200, "学号不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500201, "密码错误");
    public static CodeMsg SESSION_ERROR = new CodeMsg(500202, "Session不存在或者已经失效");
    //选课模块5003**
    public static CodeMsg QUERY_NULL = new CodeMsg(500300, "搜索内容为空");
    public static CodeMsg CHOCOURSE_OVER = new CodeMsg(500301, "课程已被选完");
    public static CodeMsg REPEATE_CHOCOURSE = new CodeMsg(500302, "不能重复选课");
    public static CodeMsg REACH_LIMIT = new CodeMsg(500303, "超出选课限额");
    public static CodeMsg CANNOT_LOAD = new CodeMsg(500304, "无法获取选课限额，请重新登录");
    public static CodeMsg REPEATE_QUITCOURSE = new CodeMsg(500305, "不能重复退选");
    public static CodeMsg TOO_EARLY = new CodeMsg(500306, "选课还未开始");
    public static CodeMsg TOO_LATE = new CodeMsg(500307, "选课已经结束");
    public static CodeMsg TRY_AGAIN = new CodeMsg(500308, "请重新尝试");

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }
}
