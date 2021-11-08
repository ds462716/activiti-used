package com.dragon.activiti.demo.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dragon.activiti.demo.dto.ProcessDefDTO;
import com.dragon.activiti.demo.service.ProcessService;
import com.dragon.activiti.demo.util.CommonConstant;
import com.dragon.activiti.demo.util.ProcessStatusEnum;
import com.dragon.activiti.demo.util.ResourceTypeEnum;
import lombok.AllArgsConstructor;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProcessServiceImpl implements ProcessService {

    private final RepositoryService repositoryService;
    private final RuntimeService runtimeService;

    /**
     * 分页流程列表
     *
     * @param params
     * @return
     */
    @Override
    public IPage<ProcessDefDTO> getProcessByPage(Map<String, Object> params) {
        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery()
                //暂不支持多租户
                //.processDefinitionTenantId(String.valueOf(TenantContextHolder.getTenantId()))
                .latestVersion();

        String category = MapUtil.getStr(params, "category");
        if (StrUtil.isNotBlank(category)) {
            query.processDefinitionCategory(category);
        }

        int page = MapUtil.getInt(params, CommonConstant.CURRENT);
        int limit = MapUtil.getInt(params, CommonConstant.SIZE);

        IPage result = new Page(page, limit);
        result.setTotal(query.count());

        List<ProcessDefDTO> deploymentList = query.listPage((page - 1) * limit, limit)
                .stream()
                .map(processDefinition -> {
                    Deployment deployment = repositoryService.createDeploymentQuery()
                            .deploymentId(processDefinition.getDeploymentId()).singleResult();
                    return ProcessDefDTO.toProcessDefDTO(processDefinition, deployment);
                }).collect(Collectors.toList());
        result.setRecords(deploymentList);
        return result;
    }

    /**
     * 读取xml/image资源
     *
     * @param procDefId
     * @param proInsId
     * @param resType
     * @return
     */
    @Override
    public InputStream readResource(String procDefId, String proInsId, String resType) {

        if (StrUtil.isBlank(procDefId)) {
            ProcessInstance processInstance = runtimeService
                    .createProcessInstanceQuery()
                    .processInstanceId(proInsId)
                    .singleResult();
            procDefId = processInstance.getProcessDefinitionId();
        }
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(procDefId)
                .singleResult();

        String resourceName = "";
        if (ResourceTypeEnum.IMAGE.getType().equals(resType)) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (ResourceTypeEnum.XML.getType().equals(resType)) {
            resourceName = processDefinition.getResourceName();
        }

        InputStream resourceAsStream = repositoryService
                .getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        return resourceAsStream;
    }

    /**
     * 更新状态
     *
     * @param status
     * @param procDefId
     * @return
     */
    @Override
    public Boolean updateStatus(String status, String procDefId) {
        if (ProcessStatusEnum.ACTIVE.getStatus().equals(status)) {
            repositoryService.activateProcessDefinitionById(procDefId, true, null);
        } else if (ProcessStatusEnum.SUSPEND.getStatus().equals(status)) {
            repositoryService.suspendProcessDefinitionById(procDefId, true, null);
        }
        return Boolean.TRUE;
    }

    /**
     * 删除部署的流程，级联删除流程实例
     *
     * @param deploymentId
     * @return
     */
    @Override
    public Boolean removeProcIns(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return Boolean.TRUE;
    }

    /**
     * 启动流程
     *
     * @param deploymentId
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveStartProcess(String deploymentId) {
        /*LeaveBill leaveBill = leaveBillMapper.selectById(leaveId);
        leaveBill.setState(TaskStatusEnum.CHECK.getStatus());

        String key = leaveBill.getClass().getSimpleName();
        String businessKey = key + "_" + leaveBill.getLeaveId();*/

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                //.processDefinitionId(processDefinitionId)
                .singleResult();


        String key = processDefinition.getKey();
        String businessKey = key + "_" + RandomUtil.randomUUID();
        runtimeService.startProcessInstanceByKeyAndTenantId(
                key,
                businessKey,
                null  //暂不支持多租户 //String.valueOf(TenantContextHolder.getTenantId())
                );

        /*leaveBillMapper.updateById(leaveBill);*/

        return Boolean.TRUE;
    }

}
