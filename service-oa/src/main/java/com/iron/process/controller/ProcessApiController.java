package com.iron.process.controller;

import com.iron.model.process.OaProcessTemplate;
import com.iron.process.service.OaProcessTemplateService;
import com.iron.process.service.OaProcessTypeService;
import com.iron.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "审批流管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin  //跨域
public class ProcessApiController {
// 这个类是用于，用户端进行登录
    @Autowired
    private OaProcessTypeService oaProcessTypeService;

    @Autowired
    private OaProcessTemplateService oaProcessTemplateService;

    @ApiOperation(value = "获取全部审批分类和其对应的模板")
    @GetMapping("findProcessType")
    public Result findProcessType() {

        return Result.ok(oaProcessTypeService.findProcessType());
    }

    @ApiOperation(value = "获取审批模板")
    @GetMapping("getProcessTemplate/{processTemplateId}")
    public Result get(@PathVariable Long processTemplateId) {

        return Result.ok(oaProcessTemplateService.getById(processTemplateId));
    }
}
