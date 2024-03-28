package com.iron.process.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.iron.model.process.OaProcessTemplate;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */
public interface OaProcessTemplateService extends IService<OaProcessTemplate> {


    void publish(long id);

    IPage<OaProcessTemplate> selectPage(Page<OaProcessTemplate> pageParam);
}
