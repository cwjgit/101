package com.leyou.common.threadlocals;

/**
 * @创建人 cwj
 * @创建时间 2019/9/16  9:09
 * @描述 用来操作线程容器的工具类
 */
public class UserHolder {
    public static final ThreadLocal<Long> TL = new ThreadLocal<>();

    /**
     * 在线程容器中放入当前用户的id
     * @param uid
     */
    public static void setUser(Long uid){
        TL.set(uid);
    }

    /**
     * 在线程容器中获取当前线程的用户id
     * @return
     */
    public static Long getUser(){
        return TL.get();
    }

    /**
     * 在线程容器中删除数据 如果不删除会造成内存泄漏 最终会内存溢出
     */
    public static void removeUser(){
        TL.remove();
    }
}
