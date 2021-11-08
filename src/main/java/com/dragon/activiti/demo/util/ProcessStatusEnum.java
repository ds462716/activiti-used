package com.dragon.activiti.demo.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProcessStatusEnum {

    ACTIVE("active"),

    SUSPEND("suspend");

    /**
     * 类型
     */
    private final String status;

}
