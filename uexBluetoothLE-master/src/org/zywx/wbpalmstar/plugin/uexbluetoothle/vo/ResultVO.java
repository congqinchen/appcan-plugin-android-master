package org.zywx.wbpalmstar.plugin.uexbluetoothle.vo;

import java.io.Serializable;

/**
 * Created by yanlongtao on 2015/4/14.
 */
public class ResultVO<T> implements Serializable {
    public static final int RESULT_OK=0;
    public static final int RESULT_FAILD=1;
    private int resultCode;
    private T data;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
