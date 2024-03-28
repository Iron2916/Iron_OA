package com.iron.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.model.process.OaProcessTemplate;
import com.iron.process.service.OaProcessTemplateService;
import com.iron.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */

@Api(value = "审批模板管理", tags = "审批模板管理")
@RestController
@CrossOrigin
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {

    @Autowired
    OaProcessTemplateService oaProcessTemplateService;
    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit) {
        
        Page<OaProcessTemplate> pageParam = new Page<>(page, limit);

        // 这里要联合查询oa_process_tmplate表,获得对象的类型.
        IPage<OaProcessTemplate> pages = oaProcessTemplateService.selectPage(pageParam);
        System.out.println("pages = " + pages.getRecords());
        return Result.ok(pages);
    }



    //@PreAuthorize("hasAuthority('bnt.processTemplate.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {

        final OaProcessTemplate processTemplate = oaProcessTemplateService.getById(id);
        return Result.ok(processTemplate);
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody OaProcessTemplate processTemplate) {

        oaProcessTemplateService.save(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody OaProcessTemplate processTemplate) {

        oaProcessTemplateService.updateById(processTemplate);
        return Result.ok();
    }

    //@PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {

        oaProcessTemplateService.removeById(id);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.publish')")
    @ApiOperation(value = "审批模板的发布")
    @PostMapping("/publish/{id}")
    public Result publish(@PathVariable long id) {
        // 发布：将模板 status 改为 1 代表发布， 并且 deploy activity

        oaProcessTemplateService.publish(id);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "上传流程定义")
    @PostMapping("uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {

        // 上传 activity 的 zip 文件
        final String path = new File(ResourceUtils.getURL("classpath:").getPath()).getAbsolutePath(); // 获得classpath路径

        final String fileName = file.getOriginalFilename();      // 获得fileName

        final File tempFile = new File(path + "/processes/");   // 上传目录

        if (!tempFile.exists()) {   // 没有此目录就创建目录

            tempFile.mkdirs();
        }

        final File imageFile = new File(path + "/processes/" + fileName);   // 创建空文件用于写入文件

        try {

            file.transferTo(imageFile);     // 将上传的文件保存在此文件中
        } catch (IOException e) {

            Result.fail("上传文件失败！");
            e.printStackTrace();
        }

        // 返回上传信息给前端
        Map<String, Object> map = new HashMap<>();
        map.put("processDefinitionPath", "processes/" + fileName);      //  返回路径
        map.put("processDefinitionKey", fileName.substring(0, fileName.lastIndexOf(".")));  // 返回名称
        return Result.ok(map);
    }
}

