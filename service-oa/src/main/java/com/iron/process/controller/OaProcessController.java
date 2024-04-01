package com.iron.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iron.process.service.OaProcessService;
import com.iron.result.Result;
import com.iron.vo.process.ApprovalVo;
import com.iron.vo.process.ProcessFormVo;
import com.iron.vo.process.ProcessQueryVo;
import com.iron.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author iron
 * @since 2024-03-28
 */
@Api(tags = "审批流管理")
@CrossOrigin
@RestController
@RequestMapping(value = "/admin/process")
public class OaProcessController {

    @Autowired
    OaProcessService oaProcessService;

    @PreAuthorize("hasAuthority('bnt.process.list')")
    @ApiOperation(value = "获取分页列表")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable long page, @PathVariable long limit, ProcessQueryVo processQueryVo) {

        Page<ProcessQueryVo> pageParm = new Page<>(page, limit);
        IPage<ProcessVo> pageModel = oaProcessService.select(pageParm, processQueryVo);

        return Result.ok(pageModel);
    }

    @ApiOperation(value = "启动流程")
    @PostMapping("/startUp")
    public Result start(@RequestBody ProcessFormVo processFormVo) throws WxErrorException {
        // 根据传递过来的审批模板类型，进行启动activity流程

        oaProcessService.startUp(processFormVo);
        return Result.ok();
    }

    @ApiOperation(value = "查询待处理列表")
    @GetMapping("/findPending/{page}/{limit}")
    public Result findPending(@PathVariable Long page, @PathVariable Long limit) {

        final Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findPending(pageParam));
    }

    @ApiOperation("获取审批详情")
    @GetMapping("show/{id}")
    public Result show(@PathVariable Long id) {

        return Result.ok(oaProcessService.show(id));
    }

    @ApiOperation(value = "审批和拒绝按钮")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo) throws WxErrorException {
        oaProcessService.approve(approvalVo);
        return Result.ok();
    }

    @ApiOperation(value = "查询已发起的任务")
    @GetMapping("/findStarted/{page}/{limit}")
    public Result findStarted(@PathVariable Long page, @PathVariable Long limit) {

        Page<ProcessQueryVo> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findStarted(pageParam));
    }

    @ApiOperation(value = "已处理")
    @GetMapping("/findProcessed/{page}/{limit}")
    public Result findProcessed(@PathVariable Long page, @PathVariable Long limit) {
        Page<Process> pageParam = new Page<>(page, limit);
        return Result.ok(oaProcessService.findProcessed(pageParam));
    }
}

