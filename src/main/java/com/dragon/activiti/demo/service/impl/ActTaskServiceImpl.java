package com.dragon.activiti.demo.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.activiti.demo.dto.TaskDTO;
import com.dragon.activiti.demo.service.ActTaskService;
import com.dragon.activiti.demo.util.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ActTaskServiceImpl implements ActTaskService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;

    @Override
    public IPage getTaskByName(Map<String, Object> params, String name) {
        int page = MapUtil.getInt(params, CommonConstant.CURRENT);
        int limit = MapUtil.getInt(params, CommonConstant.SIZE);

        TaskQuery taskQuery = taskService.createTaskQuery()
                .taskCandidateOrAssigned(name)
                //暂不支持多租户
                //.taskTenantId(String.valueOf(TenantContextHolder.getTenantId()))
                ;

        IPage result = new Page(page, limit);
        result.setTotal(taskQuery.count());

        List<TaskDTO> taskDTOList = taskQuery.orderByTaskCreateTime().desc()
                .listPage((page - 1) * limit, limit).stream().map(task -> {
                    TaskDTO dto = new TaskDTO();
                    dto.setTaskId(task.getId());
                    dto.setTaskName(task.getName());
                    dto.setProcessInstanceId(task.getProcessInstanceId());
                    dto.setNodeKey(task.getTaskDefinitionKey());
                    dto.setCategory(task.getCategory());
                    dto.setTime(task.getCreateTime());
                    return dto;
                }).collect(Collectors.toList());
        result.setRecords(taskDTOList);
        return result;
    }

    /**
     * 提交任务
     *
     * @return
     */
    @Override
    public Boolean handleTask(JSONObject paramJson) {
        String taskId = paramJson.getStr("taskId");
        String comment = paramJson.getStr("comment");

        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .singleResult();

        String processInstanceId = task.getProcessInstanceId();
        //设置下一步处理人？
        //Authentication.setAuthenticatedUserId(SecurityUtils.getUsername());
        taskService.addComment(taskId, processInstanceId, comment);

        //任务流转时，需要的表单变量
        Map<String, Object> variables = new HashMap<>(1);
        /*variables.put("flag", leaveBillDto.getTaskFlag());
        variables.put("days", leaveBillDto.getDays());*/

        taskService.complete(taskId, variables);

        //处理任务后，如果流程为null，根据流程是结束还是驳回，更新业务表的状态
        /*ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        if (pi == null) {
            LeaveBill bill = new LeaveBill();
            bill.setLeaveId(id);
            bill.setState(StrUtil.equals(TaskStatusEnum.OVERRULE.getDescription()
                    , leaveBillDto.getTaskFlag()) ? TaskStatusEnum.OVERRULE.getStatus()
                    : TaskStatusEnum.COMPLETED.getStatus());
            leaveBillMapper.updateById(bill);
        }*/
        return null;
    }

}
