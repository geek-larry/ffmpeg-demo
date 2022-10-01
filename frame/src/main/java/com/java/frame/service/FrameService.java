package com.java.frame.service;

import net.coobird.thumbnailator.Thumbnails;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.opencv_core.Mat;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class FrameService {

    public void frame() throws IOException {

        String dir = "files/";

        String videoPath = "qq.mp4";
        try (FFmpegFrameGrabber ff = FFmpegFrameGrabber.createDefault(videoPath)) {

            ff.start();
            int ffLength = ff.getLengthInFrames();
            Frame f;
            int i = 0;
            while (i < ffLength) {
                f = ff.grabImage();
                if ((i > 5) && (f.image != null)) {
                    String pngPath;
                    pngPath = i + ".png";
                    doExecuteFrame(f, dir + pngPath);
                    break;
                }
                i++;
            }
            ff.stop();
        }
        //压缩视频缩略图
        Thumbnails.of(new File(""))
                .scale(0.5f)
                .outputQuality(0.1f)
                .toOutputStream(Files.newOutputStream(Paths.get("files/qe.png")));
    }

    private static void doExecuteFrame(Frame f, String targerFilePath) {
        String imagemat = "png";
        if (null == f || null == f.image) {
            return;
        }
        BufferedImage bi;
        try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
            bi = converter.getBufferedImage(f);
        }
        File output = new File(targerFilePath);
        try {
            ImageIO.write(bi, imagemat, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage matToBufferedImage(org.opencv.core.Mat frame) {
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);
        return image;
    }

    public Mat bufferedImageToMat(BufferedImage bi) {
        try (OpenCVFrameConverter.ToMat cv = new OpenCVFrameConverter.ToMat()) {
            try (Java2DFrameConverter converter = new Java2DFrameConverter()) {
                return cv.convertToMat(converter.convert(bi));
            }
        }
    }
}
