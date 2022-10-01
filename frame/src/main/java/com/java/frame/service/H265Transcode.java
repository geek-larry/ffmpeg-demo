package com.java.frame.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Slf4j
@Service
public class H265Transcode {

    public void videoCode(String sourcePath, String targetPath) {
        Runtime run = null;
        try {
            run = Runtime.getRuntime();
            File ffmpegcmd = new File("D:\\soft\\ffmpeg\\bin\\ffmpeg.exe");
            Process p = run.exec(ffmpegcmd.getAbsolutePath() + " -i " + sourcePath + " -vcodec libx265 " + targetPath);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line;
            while ((line = br.readLine()) != null) {
                log.info("exec {}", line);
            }
            br.close();
            p.getOutputStream().close();
            p.getInputStream().close();
            p.getErrorStream().close();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert run != null;
            run.freeMemory();
        }
    }

}
