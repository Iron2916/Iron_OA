package com.iron.process.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iron.model.process.OaProcessTemplate;
import com.iron.model.process.OaProcessType;

import com.iron.process.mapper.OaProcessTypeMapper;
import com.iron.process.service.OaProcessTemplateService;
import com.iron.process.service.OaProcessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, OaProcessType> implements OaProcessTypeService {

    @Autowired
    OaProcessTemplateService oaProcessTemplateService;

    @Override
    public Object findProcessType() {

        final List<OaProcessType> typeList = this.list();

        for (OaProcessType oaProcessType : typeList) {

            LambdaQueryWrapper<OaProcessTemplate> wrapper = new LambdaQueryWrapper<>(); // 查询该类型对应的 模板
            wrapper.eq(OaProcessTemplate::getProcessTypeId, oaProcessType.getId());
            final List<OaProcessTemplate> templateList = oaProcessTemplateService.list(wrapper);

            oaProcessType.setProcessTemplateList(templateList);
        }

        return typeList;
    }
}
