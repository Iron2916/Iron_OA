package com.iron.process.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.process.OaProcess;
import com.iron.vo.process.ApprovalVo;
import com.iron.vo.process.ProcessFormVo;
import com.iron.vo.process.ProcessQueryVo;
import com.iron.vo.process.ProcessVo;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author iron
 * @since 2024-03-28
 */
public interface OaProcessService extends IService<OaProcess> {

    IPage<ProcessVo> select(Page<ProcessQueryVo> pageParm, ProcessQueryVo processQueryVo);


    void deployByZip(String processDefinitionPath);

    void startUp(ProcessFormVo processFormVo) throws WxErrorException;

    Object findPending(Page<Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo) throws WxErrorException;

    IPage<ProcessVo> findStarted(Page<ProcessQueryVo> pageParam);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);
}
