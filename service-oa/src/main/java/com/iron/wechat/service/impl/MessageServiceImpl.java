package com.iron.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iron.auth.service.SysUserService;
import com.iron.custom.LoginUserInfoHelper;
import com.iron.model.process.OaProcess;
import com.iron.model.process.OaProcessTemplate;
import com.iron.model.system.SysUser;
import com.iron.process.service.OaProcessService;
import com.iron.process.service.OaProcessTemplateService;
import com.iron.wechat.service.MessageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private OaProcessService processService;

    @Resource
    private OaProcessTemplateService processTemplateService;

    @Resource
    private SysUserService sysUserService;


    // 发送给下一个审批人
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) throws WxErrorException {
        OaProcess process = processService.getById(processId);
        OaProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        String openid = sysUser.getOpenId();

        //方便测试，给默认值（开发者本人的openId）
        if(StringUtils.isEmpty(openid)) {
            openid = "olfAI6aYSRqr3isaB9jkSsE8KqCA";
        }

        // 发送 到前端的信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
        .toUser(openid)//要推送的用户openid
        .templateId("zfeWTZN0dgyPd_Cn0fyyiXOGhGsd0xkL1G0Sf2CZrt8")//模板id，自己在网页中上设置的
        .url("https://ff9j5r07upiw.ngrok.xiaomiqiu123.top/#/show/"+processId+"/"+taskId)//点击模板消息要访问的网址，前端9090地址
        .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", submitSysUser.getName()+"提交了"+processTemplate.getName()+"审批申请，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
         templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }

    // 审批完成，时候发送
    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) throws WxErrorException {
        OaProcess process = processService.getById(processId);
        OaProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "olfAI6aYSRqr3isaB9jkSsE8KqCA";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("XDs0IFmeuB4-Km4kE4W_mx43ZS19PJHBoUdTGcNAMgk")//模板id
                .url("https://ff9j5r07upiw.ngrok.xiaomiqiu123.top/#/show/"+processId+"/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }

}