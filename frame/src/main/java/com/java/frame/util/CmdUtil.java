package com.java.frame.util;

import com.java.frame.domain.VideoInfo;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import java.io.*;
import java.util.List;

/**
 * @description: 获取视频信息，基于ffmpeg打印信息，给予FrameGrabber
 * @date: 2022/10/17
 **/
@Slf4j
@UtilityClass
public class CmdUtil {

    public void cmdTest() throws IOException {
        System.out.println("==================");
        CommandLine cmdLine = new CommandLine("ping");
        cmdLine.addArgument("www.baidu.com");
//        cmdLine.addArgument("-t");
        //cmdLine.setSubstitutionMap(Collections.singletonMap("host", "www.baidu.com"));

        // 阻塞
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        // 看门狗(timeout)
        ExecuteWatchdog watchdog = new ExecuteWatchdog(10 * 1000);

        defaultExecutor.setWatchdog(watchdog);
        //defaultExecutor.setExitValue(1); //不设置默认为0
        // 获取命令运行中的流程(一般流操作会在多线程中操作)
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errorOutPutStream = new ByteArrayOutputStream();
        PumpStreamHandler pumpStreamHandler = new PumpStreamHandler(outputStream, errorOutPutStream);

        defaultExecutor.setStreamHandler(pumpStreamHandler);
        int exitValue = defaultExecutor.execute(cmdLine);
        System.out.println(exitValue); //返回的结果


        System.out.println(outputStream.toString("gbk"));
        System.out.println(errorOutPutStream.toString("gbk"));

    }

    public VideoInfo getVideoInfoByFile(File file) {
        VideoInfo info = new VideoInfo();
        FFmpegFrameGrabber grabber = null;
        try {
            grabber = new FFmpegFrameGrabber(file);
            grabber.start();

            int lengthInFrames = grabber.getLengthInVideoFrames();
            double frameRate = grabber.getVideoFrameRate();
            double duration = grabber.getLengthInTime() / 1000000.00;
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            int audioChannel = grabber.getAudioChannels();
            String videoCode = grabber.getVideoCodecName();
            String audioCode = grabber.getAudioCodecName();
            int sampleRate = grabber.getSampleRate();

            info.setLengthInFrames(lengthInFrames);
            info.setFrameRate(frameRate);
            info.setDuration(duration);
            info.setWidth(width);
            info.setHeight(height);
            info.setAudioChannel(audioChannel);
            info.setVideoCode(videoCode);
            info.setAudioCode(audioCode);
            info.setSampleRate(sampleRate);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FFmpegFrameGrabber.Exception e) {
                log.error("getVideoInfo grabber.release failed");
            }
        }
        return info;
    }

    public String mediaInfo(String filePath) {
        //注意要保留单词之间有空格
        List<String> commend = new java.util.ArrayList<>();
        commend.add("ffprobe");
        commend.add("-i");
        commend.add(filePath);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commend);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader buf;
            String line;
            buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder sb = new StringBuilder();
            while ((line = buf.readLine()) != null) {
                System.out.println(line);
                sb.append(line);
                if (line.contains("fps")) {
                    String[] split = line.split(",");
                    for (String d : split) {
                        if (d.contains("fps")) {
                            String[] split1 = d.split(" fps");
                            String fps = split1[0];
                        }
                    }
                }
            }
            p.waitFor();
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void getVideoInfo(File file) {
        try {
            FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(file); // 获取视频文件
            frameGrabber.start();
            System.out.println(frameGrabber.getFrameRate()); // 视频帧数
            System.out.println(frameGrabber.getLengthInTime() / (1000 * 1000)); // 时长
            frameGrabber.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
