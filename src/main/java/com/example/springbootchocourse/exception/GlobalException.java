package com.example.springbootchocourse.exception;

import com.example.springbootchocourse.result.CodeMsg;

/**
 *自定义全局异常类
 */
public class GlobalException extends RuntimeException{
    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
