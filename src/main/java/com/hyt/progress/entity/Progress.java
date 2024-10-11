package com.hyt.progress.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors
@Data
public class Progress implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前进度，进度范围：0-100
     */
    private int current;

    /**
     * 执行失败原因
     */
    private boolean success = true;

    /**
     * 提示信息
     */
    private String msg;

    private Object data;
}
