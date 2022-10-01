package com.jade.m3u8.domain;

import lombok.Data;

@Data
public class TranscodeDTO {

    // 截取封面的时间			HH:mm:ss
    private String poster;

    // ts分片大小，单位是秒
    private String tsSeconds;

    // 视频裁剪，开始时间		HH:mm:ss
    private String cutStart;

    // 视频裁剪，结束时间		HH:mm:ss
    private String cutEnd;

}

