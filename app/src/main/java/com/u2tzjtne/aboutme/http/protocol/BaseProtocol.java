package com.u2tzjtne.aboutme.http.protocol;

import com.u2tzjtne.aboutme.http.HttpHelper;
import com.u2tzjtne.aboutme.util.IOUtil;
import com.u2tzjtne.aboutme.util.StringUtil;
import com.u2tzjtne.aboutme.util.UIUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 访问网络的基类
 *
 * @author Kevin
 */
public abstract class BaseProtocol<T> {

    /**
     * 获取数据
     *
     * @param index 分页请求数据的起始位置
     */
    public T getData(int index, boolean forCache) {
        // 先从本地缓存中读取数据,如果有,就直接返回,如果没有,才从网络加载
        String result = null;
        if (forCache) {
            result = getCache(index);
            if (result == null) {
                result = getDataFromNet(index, forCache);
            }
        } else {
            result = getDataFromNet(index, forCache);
        }
        if (result != null) {
            return parseJson(result);
        }
        return null;
    }

    /**
     * 从本地缓存中读取数据
     */
    private String getCache(int index) {
        // 获取系统缓存目录
        File cacheDir = UIUtil.getContext().getCacheDir();
        // 以网络链接作为文件名称,保证特定接口对应特定数据
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        if (cacheFile.exists()) {// 缓存文件存在
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(cacheFile));
                String validTime = reader.readLine();// 读取第一行内容,缓存截止时间
                if (System.currentTimeMillis() < Long.parseLong(validTime)) {// 当前时间小于缓存截止时间,说明缓存还在有效期范围内

                    String line = null;
                    StringBuffer sb = new StringBuffer();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }

                    return sb.toString();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtil.close(reader);
            }
        }

        return null;
    }

    /**
     * 向本地缓存写数据
     */
    private void setCache(String result, int index) {
        // 获取系统缓存目录
        File cacheDir = UIUtil.getContext().getCacheDir();
        // 以网络链接作为文件名称,保证特定接口对应特定数据
        File cacheFile = new File(cacheDir, getKey() + "?index=" + index
                + getParams());

        FileWriter writer = null;
        try {
            writer = new FileWriter(cacheFile);

            // 缓存有效期限, 截止时间设定为半小时之后
            long validTime = System.currentTimeMillis() + 30 * 60 * 1000;
            writer.write(validTime + "\n");// 将缓存截止时间写入文件第一行
            writer.write(result);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.close(writer);
        }
    }

    /**
     * 访问网络获取数据
     *
     * @param index 分页请求数据的起始位置
     * @return
     */
    private String getDataFromNet(int index, boolean setCache) {
        HttpHelper.HttpResult result = HttpHelper.get(HttpHelper.URL + getKey() + "?index=" + index + getParams());
        if (result != null) {
            String strResult = result.getString();
            if (!StringUtil.isEmpty(strResult)) {
                // 将缓存写到本地文件中
                if (setCache) {
                    setCache(strResult, index);
                }
                return strResult;
            }
        }
        return null;
    }

    // 获取网络接口的具体地址,每个页面都不一样,必须由子类实现
    public abstract String getKey();

    // 获取网络接口的具体参数,每个页面都不一样,必须由子类实现
    public abstract String getParams();

    /**
     * 解析json数据 ,每个页面要求的解析对象都不一样,必须由子类实现
     *
     * @param result
     */
    public abstract T parseJson(String result);

}
