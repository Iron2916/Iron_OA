package com.iron.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.process.OaProcessType;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */
public interface OaProcessTypeService extends IService<OaProcessType> {

    Object findProcessType();
}
