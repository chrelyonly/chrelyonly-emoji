package cn.chrelyonly.chrelyonlyemoji.util;


import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.io.Serializable;

/**
 * @author chrelyonly
 */
@Data
public class R<T> implements Serializable {

    private int code = 200;
    private boolean success = true;
    private T data = null;
    private String msg = "暂无数据";
    private long time = System.currentTimeMillis();

    private R(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.success = ResultCode.SUCCESS.code == code;
    }

    public static <T> R<T> data(T data) {
        return data(data, "操作成功");
    }

    public static <T> R<T> data(T data, String msg) {
        return data(200, data, msg);
    }

    public static <T> R<T> data(int code, T data, String msg) {
        return new R(code, data, data == null ? "暂无承载数据" : msg);
    }

    public static <T> R<T> success(String msg) {
        return new R(200, null, msg);
    }

    public static <T> R<T> success() {
        return new R(200, null, "操作成功");
    }

    public static <T> R<T> ok(String msg) {
        return new R(200, null, msg);
    }

    public static <T> R<T> ok() {
        return new R(200, null, "操作成功");
    }

    public static <T> R<T> fail(String msg) {
        return new R(500, null, msg);
    }
    public static <T> R<T> fail(Object data) {
        return new R(500, data, "操作失败");
    }
    public static <T> R<T> fail() {
        return new R(500, null, "操作失败");
    }
    public static <T> R<T> fail(String msg,int code) {
        return new R(code, null, msg);
    }
    public static <T> R<T> error(String msg) {
        return new R(500, null, msg);
    }
    public static <T> R<T> error(String msg,int code) {
        return new R(code, null, msg);
    }

    public static <T> R<T> status(boolean flag) {
        return flag ? success("操作成功") : fail("操作失败");
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
