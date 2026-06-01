package com.flowx.common.core.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * File type enumeration
 *
 * @author FlowX
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum FileTypeEnum {

    /**
     * Image file
     */
    IMAGE(1, "图片"),

    /**
     * Document file
     */
    DOCUMENT(2, "文档"),

    /**
     * Video file
     */
    VIDEO(3, "视频"),

    /**
     * Audio file
     */
    AUDIO(4, "音频"),

    /**
     * Other file type
     */
    OTHER(0, "其他");

    /**
     * Type code
     */
    @EnumValue
    private final int code;

    /**
     * Type description
     */
    private final String description;
}
