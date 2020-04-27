/*
 * @Author: shenzheng
 * @Date: 2020/4/27 23:20
 */

package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//序列化json对象空值消失
public class ServerResponse <T> implements Serializable {
    private  int status;
    private String msg;
    private T data;

    private  ServerResponse(int status){
        this.status =status;
    }
    private  ServerResponse(int status,T data){
        this.status =status;
        this.data =data;
    }
    private  ServerResponse(int status,String msg,T data){
        this.status =status;
        this.msg=msg;
        this.data =data;
    }
    private  ServerResponse(int status,String msg){
        this.status =status;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isSuccess(){
        return  this.status==ResponseCode.SUCCESS.getCode();
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }
    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }
    public static <T> ServerResponse<T> createBySuccessMessage(String message){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),message);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }
    public static <T> ServerResponse<T> createByErrorMessage(String msg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),msg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int error,String msg){
        return new ServerResponse<T>(error,msg);
    }


}
