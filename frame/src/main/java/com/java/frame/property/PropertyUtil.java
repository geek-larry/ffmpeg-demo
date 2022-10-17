package com.java.frame.property;

import lombok.experimental.UtilityClass;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @description:
 * @date: 2022/10/17
 **/
@UtilityClass
public class PropertyUtil {

    public static Properties properties = null;

    public void readConfigFile(String configFileName) {
        //读取配置文件
        FileInputStream fileInputStream = null;
        InputStream inputStream = null;
        try {
            fileInputStream = new FileInputStream(configFileName);
            inputStream = new BufferedInputStream(fileInputStream);
            properties = new Properties();
            properties.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getValue(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
