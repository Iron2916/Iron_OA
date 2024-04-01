package com.iron.process.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.model.process.OaProcessTemplate;
import com.iron.model.process.OaProcessType;
import com.iron.process.mapper.OaProcessTemplateMapper;
import com.iron.process.service.OaProcessService;
import com.iron.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iron.process.service.OaProcessTypeService;
import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, OaProcessTemplate> implements OaProcessTemplateService {

    @Autowired
    OaProcessTypeService oaProcessTypeService;

    @Autowired
    OaProcessService oaProcessService;
    @Override
    public void publish(long id) {
        // 第一更新Template status, 第二部署activity

        final OaProcessTemplate template = this.getById(id);
        template.setStatus(1);  // 代表已经发布
        this.updateById(template);

        // 进行部署,对应的activity
        if (!StringUtils.isEmpty(template.getProcessDefinitionPath())) {

            oaProcessService.deployByZip(template.getProcessDefinitionPath());
        }

    }

    @Override
    public IPage<OaProcessTemplate> selectPage(Page<OaProcessTemplate> pageParam) {

        // ----------------------- 自己写的时间复杂度为 n^2 用了两层for循环 ------------------------
/*

        // 获得所有的流程类型
        final List<OaProcessType> typesList = oaProcessTypeService.list();
        // 获得查询出来的数据
        final IPage<OaProcessTemplate> pages = this.page(pageParam);
        final List<OaProcessTemplate> tmplateList = pages.getRecords();


        // 循环遍历进行设置相关的类型名称
        for (int i=0; i<tmplateList.size(); i++) {

            for (OaProcessType type : typesList) {

                if (tmplateList.get(i).getProcessTypeId().intValue() == type.getId().intValue()) {

                    pages.getRecords().get(i).setProcessTypeName(type.getName());
                }
            }
        }
*/

        // -------------------------- 参考标准进行修改，时间复杂度修改为 n

        final List<OaProcessType> oaProcessTypes = oaProcessTypeService.list();

        final Page<OaProcessTemplate> pages = this.page(pageParam);
        final List<OaProcessTemplate> records = pages.getRecords();

        // 将获得到的 流程类型转换为 Map 表
        final Map<Long, String> typeMap = oaProcessTypes.stream().collect(Collectors.toMap(OaProcessType::getId, OaProcessType::getName));

        for (int i=0; i<records.size(); i++) {

            final String typeName = typeMap.get(records.get(i).getProcessTypeId());

            if (StringUtil.isNullOrEmpty(typeName)) continue;

            pages.getRecords().get(i).setProcessTypeName(typeName);
        }


        return pages;
    }
}
