package com.dragon.activiti.demo.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

public interface ActTaskService {

    /**
     * 获取用户代办列表
     *
     * @param params
     * @param name
     * @return
     */
    IPage getTaskByName(Map<String, Object> params, String name);

    /**
     * 处理任务
     *
     * @return
     */
    Boolean handleTask(JSONObject paramJson);
}
