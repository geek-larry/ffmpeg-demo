package com.java.frame.util;

import cn.hutool.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class FFmpegCmdUtil {

    private static final Logger log = LoggerFactory.getLogger(FFmpegCmdUtil.class);

    private static String rootPath;// D:/soft/ffmpeg/bin

    // static {
    // rootPath = FileMd5Util.getRealPath() + "WEB-INF/classes/ffmpeg";
    // }

    /**
     * 获取视频某个时间点的图片
     * timeStr 为秒 第几秒
     */
    public static String catchImage(String filePath, String picName, String size) {
        String tempDir = filePath.substring(0, filePath.lastIndexOf("/") + 1);
        String name = picName + "_postpath.jpg";
        String output = tempDir + name;
        try {
            List<String> command = new ArrayList<String>();
            command.add("cmd.exe");
            command.add("/c");
            command.add(rootPath + "/ffmpeg");
            command.add("-i");
            command.add(filePath);
            command.add("-y");
            command.add("-f");
            command.add("image2");
            command.add("-r");
            command.add("1");
            command.add("-ss");
            command.add("1");
            command.add("-t");
            command.add("1");
            command.add("-s");
            command.add(size);
            command.add(output);
            // 开始执行，并不等待返回
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private static void convertToflvHD(String filePath, String dest) {
        try {
            List<String> command = new ArrayList<String>();
            command.add("cmd.exe");
            command.add("/C");
            command.add(rootPath + "/ffmpeg");
            command.add("-i"); // 输入文件路径
            command.add(filePath);
            command.add("-y"); // 是否覆盖
            command.add("-ab"); // 音频数据流量，32 64 96 128
            command.add("32");
            command.add("-ar"); // 声音采样频率 22050
            command.add("22050");
            command.add("-acodec"); // 音频编码AAC
            command.add("aac");
            command.add("-s"); // 指定分辨率，标清，高清；
            // command.add("640x480"); //标清：480p
            command.add("1280x720"); // 高清：720p
            command.add("-qscale"); // 以数值质量为基础的VBR，范围：0.01-255，越小质量越好
            command.add("10");
            command.add("-r"); // 帧速率，数值： 15 29.97
            command.add("15");
            command.add(dest); // 目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            pb.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得视频原有的分辨率
     */
    public static String getVideoResolution(StringBuffer sb) {
        Pattern pat = Pattern.compile("([0-9]{2,4}x[0-9]{2,4})");
        Matcher matcher = pat.matcher(sb.toString());
        boolean find = matcher.find();
        if (find) {
            return matcher.group(1);
        }
        return "";
    }

    /**
     * 返回视频的长度：毫秒
     */
    public static long getVideoTime(StringBuffer sb) {
        Pattern pat = Pattern.compile("Duration: ([0-9:.]*),");
        Matcher matcher = pat.matcher(sb.toString());
        boolean find = matcher.find();
        if (find) {
            String res = matcher.group(1);
            // 处理
            String[] arr = res.split("[.]");
            String t = "";
            String l = "0";
            if (arr.length > 0) {
                t = arr[0];
                if (arr.length > 1)
                    l = arr[1];
            }
            try {
                long tempL = Long.parseLong(l);
                tempL = tempL * (l.length() == 1 ? 100 : (l.length() == 2 ? 10 : 1));
                String[] tempT = t.split(":");
                long tempH = Long.parseLong(tempT[0]);
                long tempM = Long.parseLong(tempT[1]);
                long tempS = Long.parseLong(tempT[2]);
                tempH = tempH * 60 * 60 * 1000;
                tempM = tempM * 60 * 1000;
                tempS = tempS * 1000;

                return (tempH + tempM + tempS + tempL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0L;
    }

    /**
     * 转化视频文件为Flv格式文件；
     */
    public static JSONObject convertToFlv(String filePath) {
        JSONObject result = new JSONObject();
        String tempFolder = filePath.substring(0, filePath.lastIndexOf("/"));
        String fileName = filePath.substring(filePath.lastIndexOf("/"), filePath.lastIndexOf("."));
        File folder = new File(tempFolder);
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }
        try {
            List<String> command = new ArrayList<>();
            String dest = tempFolder + fileName + ".flv";
            String destHD = tempFolder + fileName + "-HD.flv";
            System.out.println(filePath + "\r\n" + dest);
            command.add("cmd.exe");
            command.add("/C");
            command.add(rootPath + "/ffmpeg");
            command.add("-i"); // 输入文件路径
            command.add(filePath);
            command.add("-y"); // 是否覆盖
            // command.add("-ab"); //音频数据流量，32 64 96 128
            // command.add("32K");
            // command.add("-ar"); //声音采样频率 22050
            // command.add("22050");
            // command.add("-acodec"); //音频编码AAC
            // command.add("aac");
            command.add("-s"); // 指定分辨率，标清，高清；
            command.add("640x480"); // 标清：480p
            // command.add("1280x720"); //高清：720p
            // command.add("-qscale"); //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
            // command.add("10");
            command.add("-r"); // 帧速率，数值： 15 29.97
            command.add("15");
            command.add(dest); // 目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
                System.out.println(line);
            }
            p.waitFor();
            p.destroy();
            // 获得时长/速率/其他meta信息
            convertToflvHD(filePath, destHD);
            long time = getVideoTime(sb);
            String solution = getVideoResolution(sb);
            // 截取第一秒的图片做封面
            catchImage(dest, fileName, solution);
            result.put("destPath", dest); // 普情FLV存放地址
            result.put("duration", time); // 时长
            result.put("destPathHD", destHD);// 高清FLV存放地址
            result.put("solution", solution);// 分辨率
            result.put("imagePath", tempFolder + fileName + "-1-" + solution + ".jpg");
            // 转化完成后进行增加索引
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 根据当前视频的分辨率获得高清分辨率信息
    public static String getHDSolution(String solution) {
        String rs = "1280x720";
        try {
            if (solution.contains("x")) {
                String[] ars = solution.split("x");
                String width = ars[1];
                int iwidth = Integer.parseInt(width);
                if (iwidth < 720) {
                    rs = solution;
                }
            }
        } catch (Exception ignored) {
        }
        return rs;
    }

    /**
     * 新版-视频转化逻辑流程
     * 1. 至转化高清
     */
    public static JSONObject callFFmpegNew(String filePath) {
        log.info("开始转化视频");
        Date d1 = new Date();
        long d1l = d1.getTime();
        filePath = filePath.replaceAll("\\\\", "/");
        JSONObject jo = new JSONObject();
        String folder = filePath.substring(0, filePath.lastIndexOf("/"));// 文件的相对文件夹路径,例如：/attachment/video/
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));// 文件的文件名,例如：sss.mp4
        String hdDestFolder = folder + "/" + fileName + "_HD";// 高清存放路径
        String hdDestM3u8Path = hdDestFolder + "/" + fileName + ".m3u8";
        String hdResPath = folder + "/" + fileName + "_HD" + "/" + fileName + ".m3u8";

        File destFolderFile = new File(hdDestFolder);
        if (!destFolderFile.isDirectory()) {
            destFolderFile.mkdirs();
        }

        BufferedReader br;
        String solution = "1280x720";
        long duration = 0L;
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath + "/ffmpeg";
            command[3] = "-i"; // 输入文件路径
            command[4] = filePath;
            command[5] = "-c:v"; //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            // command[12] = Constant.VIDEO_HLS_TIME + "";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = hdDestM3u8Path;
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            // 获得视频的分辨率，默认分辨率1280x720
            solution = getVideoResolution(sb);
            duration = getVideoTime(sb);
            p.waitFor();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(), e);
        }
        // 通过输出流获得需要的信息
        // 截取第一秒的图片做封面
        String name = catchImage(filePath, fileName, solution);
        String postPath = folder + "/" + fileName + "_postpath.jpg";
        if (!new File(postPath).exists()) {
            postPath = folder + "/" + name;
        }
        jo.put("solution", solution);
        jo.put("postpath", postPath);
        jo.put("success", true);
        jo.put("duration", duration);
        jo.put("hdPath", hdResPath);
        jo.put("sdPath", "");

        Date d2 = new Date();
        long d2l = d2.getTime();
        long diff = d2l - d1l;
        log.debug("\r\n 视频转化转化结束，转化结果为：\r\n 文件路径:" + filePath + " \r\n 耗时: " + (diff / 1000) + "秒 \r\n 返回信息:"
                + jo);
        return jo;
    }

    /**
     * 对文件进行分片,默认处理方法：在目标文件目录下建立一个同名文件夹，然后将对应的分片数据放入文件夹内
     */
    public static JSONObject callFFmpeg(String filePath) {
        /**
         * 目标：两种分辨率格式，最终都为m3u8,时长固定,切换源，名字一样？
         * 1.将源文件转化为两个格式的flv文件，分别存储，
         * ID_HD-->id.flv-->id.m3u8
         * ID_SD-->id.flv-->id.m3u8
         * 2.将两个分辨率的flv转化为m3u8格式
         * 3.将两个m3u8格式的地址回传回去
         */
        log.info("开始转化视频");
        filePath = filePath.replaceAll("\\\\", "/");
        JSONObject jo = new JSONObject();
        // String basePath = "";//FileUtil.getConfigRealPath();//tomcat
        // 路径,例如：i:/tomcat1/resource/
        String folder = filePath.substring(0, filePath.lastIndexOf("/"));// 文件的相对文件夹路径,例如：/attachment/video/
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));// 文件的文件名,例如：sss.mp4
        // String sourcePath = basePath+filePath;//文件的绝对路径，源文件
        String hdDestFolder = folder + "/" + fileName + "_HD";// 高清存放路径
        String hdDestFlvPath = hdDestFolder + "/" + fileName + ".flv";
        String hdDestM3u8Path = hdDestFolder + "/" + fileName + ".m3u8";
        String hdResPath = folder + "/" + fileName + "_HD" + "/" + fileName + ".m3u8";
        String sdDestFolder = folder + "/" + fileName + "_SD";// 普清存放路径
        String sdDestFlvPath = sdDestFolder + "/" + fileName + ".flv";
        String sdDestM3u8Path = sdDestFolder + "/" + fileName + ".m3u8";
        String sdResPath = folder + "/" + fileName + "_SD" + "/" + fileName + ".m3u8";

        File destFolderFile = new File(hdDestFolder);
        if (!destFolderFile.isDirectory()) {
            destFolderFile.mkdirs();
        }
        File destFolderFile2 = new File(sdDestFolder);
        if (!destFolderFile2.isDirectory()) {
            destFolderFile2.mkdirs();
        }
        // 1. 转化源文件为普清FLV格式

        BufferedReader br = null;
        String solution = "";
        long duration = 0L;
        try {
            String[] command = new String[13];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath + "/ffmpeg";
            command[3] = "-i"; // 输入文件路径
            command[4] = filePath;
            command[5] = "-y"; // 是否覆盖
            // command[6] = "-ab"; //音频数据流量，32 64 96 128
            // command[7] = "32K";
            command[6] = "-ar"; // 声音采样频率 22050
            command[7] = "22050";
            // command[10] = "-acodec"; //音频编码AAC
            // command[11] = "aac";
            command[8] = "-s"; // 指定分辨率，标清，高清；
            command[9] = "640x480"; // 标清：480p
            // command[14] = "1280x720"; //高清：720p
            // command[14] = "-qscale"; //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
            // command[15] = "10";
            command[10] = "-r"; // 帧速率，数值： 15 29.97
            command[11] = "15";
            command[12] = sdDestFlvPath;// 目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            p.waitFor();
            solution = getVideoResolution(sb);
            duration = getVideoTime(sb);
            br.close();
            isr.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2. 转化源文件为高清FLV格式
        // 根据solution获得HD Solution,edit by lixun ,鉴于视频转化低分辨率后失真严重，高清不在转化其他分辨率。
        // String hdSolution = getHDSolution(solution);
        String hdSolution = solution;
        try {
            String[] command = new String[13];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath + "/ffmpeg";
            command[3] = "-i"; // 输入文件路径
            command[4] = filePath;
            command[5] = "-y"; // 是否覆盖
            // command[6] = "-ab"; //音频数据流量，32 64 96 128
            // command[7] = "32K";
            command[6] = "-ar"; // 声音采样频率 22050
            command[7] = "22050";
            // command[10] = "-acodec"; //音频编码AAC
            // command[11] = "aac";
            command[8] = "-s"; // 指定分辨率，标清，高清；
            // command[13] = "640x480"; //标清：480p
            // command[7] = "1280x720"; //高清：720p
            command[9] = hdSolution;
            // command[15] = "-qscale"; //以数值质量为基础的VBR，范围：0.01-255，越小质量越好
            // command[16] = "10";
            command[10] = "-r"; // 帧速率，数值： 15 29.97
            command[11] = "15";
            command[12] = hdDestFlvPath; // 目标存储路径
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            p.waitFor();
            br.close();
            isr.close();
            p.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 3. 转化普清FLV为m3u8格式
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath + "/ffmpeg";
            command[3] = "-i"; // 输入文件路径
            command[4] = sdDestFlvPath;
            command[5] = "-c:v"; //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            // command[12] = Constant.VIDEO_HLS_TIME + "";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = sdDestM3u8Path;

            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            p.waitFor();
            p.destroy();
            isr.close();
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 4. 转化高清FLV为m3u8格式
        try {
            String[] command = new String[18];
            command[0] = "cmd.exe";
            command[1] = "/C";
            command[2] = rootPath + "/ffmpeg";
            command[3] = "-i"; // 输入文件路径
            command[4] = hdDestFlvPath;
            command[5] = "-c:v"; //
            command[6] = "libx264";
            command[7] = "-c:a";
            command[8] = "aac";
            command[9] = "-strict";
            command[10] = "-2";
            command[11] = "-hls_time";
            // command[12] = Constant.VIDEO_HLS_TIME + "";
            command[13] = "-hls_list_size";
            command[14] = "0";
            command[15] = "-f";
            command[16] = "hls";
            command[17] = hdDestM3u8Path;
            ProcessBuilder pb = new ProcessBuilder();
            pb.command(command);
            Process p = pb.start();
            InputStream stderr = p.getErrorStream();
            InputStreamReader isr = new InputStreamReader(stderr);
            br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb);
            p.waitFor();
            p.destroy();
            isr.close();
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
            jo.put("success", false);
        }

        // 通过输出流获得需要的信息
        // 截取第一秒的图片做封面
        String name = catchImage(filePath, fileName, solution);
        String postPath = folder + "/" + fileName + "_postpath.jpg";
        if (!new File(postPath).exists()) {
            postPath = folder + "/" + name;
        }
        jo.put("solution", solution);
        jo.put("postpath", postPath);
        jo.put("success", true);
        jo.put("duration", duration);
        // jo.element("destpath",resPath);
        jo.put("hdPath", hdResPath);
        jo.put("sdPath", sdResPath);
        System.out.println("视频转化转化结束，转化结果为：\r\n" + jo.toString());
        return jo;
    }

    public static void main(String[] args) {
        rootPath = "I:/workspace/ffmpeg/src/main/resources/ffmpeg";
        String file = "d:/abc/avi/4.mp4";
        callFFmpegNew(file);
        // convertToFlv(file);
        System.out.println(rootPath);
    }
}
