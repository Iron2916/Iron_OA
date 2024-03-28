package com.iron.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.model.process.OaProcessType;
import com.iron.process.service.OaProcessTypeService;
import com.iron.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author iron
 * @since 2024-03-27
 */

@Api(value = "审批类型", tags = "审批类型")
@RestController
@CrossOrigin
@RequestMapping("/admin/process/processType")
public class OaProcessTypeController {

    @Autowired
    OaProcessTypeService oaProcessTypeService;


//    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page, @PathVariable Long limit) {

        Page<OaProcessType> pageParam = new Page<>(page,limit);
        final Page<OaProcessType> pageModel = oaProcessTypeService.page(pageParam);
        return Result.ok(pageModel);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.list')")
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {

        final OaProcessType processType = oaProcessTypeService.getById(id);
        return Result.ok(processType);
    }

//    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody OaProcessType processType) {

        oaProcessTypeService.save(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody OaProcessType processType) {

        oaProcessTypeService.updateById(processType);
        return Result.ok();
    }

//    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {

        oaProcessTypeService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value = "获取全部审批分类")
    @GetMapping("findAll")
    public Result findAll() {

        return Result.ok(oaProcessTypeService.list());
    }

}

