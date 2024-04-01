package com.iron.process.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.auth.service.SysUserService;
import com.iron.custom.LoginUserInfoHelper;
import com.iron.model.process.OaProcess;
import com.iron.model.process.OaProcessRecord;
import com.iron.model.process.OaProcessTemplate;
import com.iron.model.system.SysUser;
import com.iron.process.mapper.OaProcessMapper;
import com.iron.process.service.OaProcessRecordService;
import com.iron.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iron.process.service.OaProcessTemplateService;
import com.iron.vo.process.ApprovalVo;
import com.iron.vo.process.ProcessFormVo;
import com.iron.vo.process.ProcessQueryVo;
import com.iron.vo.process.ProcessVo;
import com.iron.wechat.service.MessageService;
import io.jsonwebtoken.lang.Collections;
import me.chanjar.weixin.common.error.WxErrorException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author iron
 * @since 2024-03-28
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, OaProcess> implements OaProcessService {

    @Autowired
    OaProcessMapper oaProcessMapper;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    OaProcessTemplateService oaProcessTemplateService;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    TaskService taskService;

    @Autowired
    OaProcessRecordService oaProcessRecordService;

    @Autowired
    HistoryService historyService;

    @Autowired
    MessageService messageService;

    @Override
    public IPage<ProcessVo> select(Page<ProcessQueryVo> pageParam, ProcessQueryVo processQueryVo) {

        IPage<ProcessVo> page = oaProcessMapper.selectPage(pageParam, processQueryVo);

        return page;
    }

    @Override
    public void deployByZip(String processDefinitionPath) {

        // 从对应的路径中取出 zip 文件
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(processDefinitionPath);
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 进行部署
        repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) throws WxErrorException {

        final SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        final OaProcessTemplate processTemplate = oaProcessTemplateService.getById(processFormVo.getProcessTemplateId());

        // 封装 process
        OaProcess process = new OaProcess();
        BeanUtils.copyProperties(processFormVo, process);   // 复制相关配置
        String workNo = System.currentTimeMillis() + "";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(sysUser.getName() + "发起" + processTemplate.getName() + "申请");
        process.setStatus(1);
        oaProcessMapper.insert(process);    // 插入 process

        // 封装数据对象
        final String businessKey = String.valueOf(process.getId()); // 这里的封装的是 process Id
        Map variables = new HashMap<>();                            // 流程变量 variable
        final JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map map = new HashMap();
        for (Map.Entry<String, Object> entry : formData.entrySet()) {

            map.put(entry.getKey(), entry.getValue());
        }
        variables.put("data", map);

        // 发起实例
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processTemplate.getProcessDefinitionKey(), businessKey, variables);
        final String processInstanceId = processInstance.getId();
        process.setProcessInstanceId(processInstanceId);

        // 通知，下一个 接受者
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstanceId).list();
        if (!Collections.isEmpty(taskList)) {

            List<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {    // 循环查询每个用户, 如果在表中存在，就加入到list集合中去

                final SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, task.getAssignee()));
                assigneeList.add(user.getName());

                // 推送消息给下一个人，后续完善
                messageService.pushPendingMessage(process.getId(), sysUser.getId(), task.getId());
            }

            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
        }

        oaProcessMapper.updateById(process);    // 更新process 相关信息

        oaProcessRecordService.record(process.getId(), 1, process.getDescription());    // 发起实例后进行记录
    }

    @Override
    public Object findPending(Page<Process> pageParam) {

        final String username = LoginUserInfoHelper.getUsername();  // 根据当前登录的用户名称查出当前的待处理任务
        final TaskQuery query = taskService.createTaskQuery().taskAssignee(username).orderByTaskCreateTime().desc();
        final List<Task> taskList = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());

        List<ProcessVo> processList = new ArrayList<>();  // 将查询到的 taskList 转换成 processList 对象返回给前端

        for (Task task : taskList) {

            // 通过 task 任务,查询出对应的 流程实例
            final ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();

            if (processInstance == null) continue;
            if (processInstance.getBusinessKey() == null) continue;
            final long businessKey = Long.parseLong(processInstance.getBusinessKey());  // 这里存储的是 对应的 ProcessId
            final OaProcess process = this.getById(businessKey);
            if (process == null) continue;
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());
            processList.add(processVo);
        }

        int totalCount = processList.size();
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);   // 封装好page对象进行返回
        page.setRecords(processList);
        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {

        final OaProcess process = this.getById(id); // 获取到相关的信息
        final List<OaProcessRecord> oaProcessRecordList = oaProcessRecordService.list(  // 获得相关的记录信息
                new LambdaQueryWrapper<OaProcessRecord>().eq(OaProcessRecord::getProcessId, id)
        );
        final OaProcessTemplate processTemplate = oaProcessTemplateService.getById(process.getProcessTemplateId());

        Map map = new HashMap();
        map.put("process", process);
        map.put("processRecordList", oaProcessRecordList);
        map.put("processTemplate", processTemplate);
        // 审批完成的任务，不能在进行二次审批，同时能查看的用户不是都能进行审批，需要进一步判断能否进行审批。
        final List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list(); // 查询当前process的task
        boolean isApprove = false;
        if (!CollectionUtils.isEmpty(taskList)) {
            for(Task task : taskList) {
                if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())) {
                    isApprove = true;
                }
            }
        }

        map.put("isApprove", isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) throws WxErrorException {

        final Map<String, Object> variable = taskService.getVariables(approvalVo.getTaskId());  // 进行打印信息
        for (Map.Entry<String, Object> stringObjectEntry : variable.entrySet()) {
            System.out.println( stringObjectEntry.getKey() + " : " + stringObjectEntry.getValue());
        }

        final String taskId = approvalVo.getTaskId();

        if (approvalVo.getStatus() == 1) {
            // 审批通过，即前端触发了，审批通过按钮

            taskService.complete(taskId);
        } else {
            //  审批拒绝，即前端点击了拒绝按钮

            this.endTask(taskId); //进行驳回，即拒绝
        }
        String description = approvalVo.getStatus().intValue() == 1 ? "已通过" : "驳回";
        oaProcessRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

        //计算下一个审批人，为微信小程序做准备
        OaProcess process = this.getById(approvalVo.getProcessId());
        final List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        if (!CollectionUtils.isEmpty(taskList)) {
            List<String> assigneeList = new ArrayList<>();
            for(Task task : taskList) {

                SysUser sysUser = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, task.getAssignee()));
                assigneeList.add(sysUser.getName());

                //推送消息给下一个审批人,调用wechat messageService
                messageService.pushProcessedMessage(process.getId(), process.getUserId(), approvalVo.getStatus());
            }

            process.setDescription("等待" + StringUtils.join(assigneeList.toArray(), ",") + "审批");
            process.setStatus(1);
        } else {
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }
        //推送消息给申请人
        this.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessQueryVo> pageParam) {

        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> page = oaProcessMapper.selectPage(pageParam, processQueryVo);

        for (ProcessVo item : page.getRecords()) {
            item.setTaskId("0");
        }
        return page;
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {

        // 根据当前人的ID查询
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery().taskAssignee(LoginUserInfoHelper.getUsername()).finished().orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        // 查询后的结果转换为 ProcessList
        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            OaProcess process = this.getOne(new LambdaQueryWrapper<OaProcess>().eq(OaProcess::getProcessInstanceId, processInstanceId));
            if (process == null) continue;
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }
        IPage<ProcessVo> page = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount);
        page.setRecords(processList);
        return page;
    }

    private void endTask(String taskId) {

        //  第一步：获取到当前的任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);  // 取到结束节点
        if(CollectionUtils.isEmpty(endEventList)) { //  如果没有结束节点
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);  //  结束节点
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());   // 当前节点


        //  第二步：结束当前任务
        List originalSequenceFlowList = new ArrayList<>();   //  临时保存当前活动的原始方向
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        currentFlowNode.getOutgoingFlows().clear(); // 清除当前活动原始方向

        //  第三步：建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);  // 当前节点
        newSequenceFlow.setTargetFlowElement(endFlowNode);      // 指向最终节点
        List newSequenceFlowList = new ArrayList<>();   // 临时保存新流程
        newSequenceFlowList.add(newSequenceFlow);

        //  第四步：当前节点指向新方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  第五步：完成任务
        taskService.complete(task.getId());
    }

}
