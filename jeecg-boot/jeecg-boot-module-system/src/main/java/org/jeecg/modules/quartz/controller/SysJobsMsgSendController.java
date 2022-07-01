package org.jeecg.modules.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.quartz.entity.JobsMsgSendModel;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.quartz.service.ISysJobsMsgSendService;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.model.AnnouncementSendModel;
import org.jeecg.modules.system.service.ISysAnnouncementSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
* @Title: Controller
* @Description: 用户通告阅读标记表
* @Author: jeecg-boot
* @Date:  2019-02-21
* @Version: V1.0
*/
@RestController
@RequestMapping("/sys/jobsMsgSend")
@Slf4j
public class SysJobsMsgSendController {
   @Autowired
   private ISysJobsMsgSendService sysJobsMsgSendService;

   /**
     * 分页列表查询
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @GetMapping(value = "/list")
   public Result<IPage<SysJobsMsgSend>> queryPageList(
                                     @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                     @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                     HttpServletRequest req) {
       Result<IPage<SysJobsMsgSend>> result = new Result<>();
       Page<SysJobsMsgSend> page = new Page<>(pageNo,pageSize);
       //排序逻辑 处理
       String anntId = req.getParameter("anntId");
       String readFlag = req.getParameter("readFlag");
       String username = req.getParameter("username");
       Map<String,Object> params = new HashMap<>();
       params.put("pageNo",pageNo);
       params.put("pageSize",pageSize);
       params.put("anntId",anntId);
       params.put("readFlag",readFlag);
       params.put("username",username);
       List<SysJobsMsgSend> list = sysJobsMsgSendService.findReadListByPage(params);
       int count = sysJobsMsgSendService.countReadListByPage(params);
       page.setRecords(list);
       page.setTotal(count);
       IPage<SysJobsMsgSend> pageList = page;
       result.setSuccess(true);
       result.setResult(pageList);
       return result;
   }

   /**
     *   添加
    * @param sysJobsMsgSend
    * @return
    */
   @PostMapping(value = "/add")
   public Result<SysJobsMsgSend> add(@RequestBody SysJobsMsgSend sysJobsMsgSend) {
       Result<SysJobsMsgSend> result = new Result<>();
       try {
           sysJobsMsgSendService.save(sysJobsMsgSend);
           result.success("添加成功！");
       } catch (Exception e) {
           log.error(e.getMessage(),e);
           result.error500("操作失败");
       }
       return result;
   }

   /**
     *  编辑
    * @param sysJobsMsgSend
    * @return
    */
   @PutMapping(value = "/edit")
   public Result<SysJobsMsgSend> eidt(@RequestBody SysJobsMsgSend sysJobsMsgSend) {
       Result<SysJobsMsgSend> result = new Result<>();
       SysJobsMsgSend old= sysJobsMsgSendService.getById(sysJobsMsgSend.getId());
       if(old==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = sysJobsMsgSendService.updateById(sysJobsMsgSend);
           //TODO 返回false说明什么？
           if(ok) {
               result.success("修改成功!");
           }
       }

       return result;
   }

   /**
     *   通过id删除
    * @param id
    * @return
    */
   @DeleteMapping(value = "/delete")
   public Result<SysJobsMsgSend> delete(@RequestParam(name="id",required=true) String id) {
       Result<SysJobsMsgSend> result = new Result<>();
       SysJobsMsgSend old = sysJobsMsgSendService.getById(id);
       if(old==null) {
           result.error500("未找到对应实体");
       }else {
           boolean ok = sysJobsMsgSendService.removeById(id);
           if(ok) {
               result.success("删除成功!");
           }
       }

       return result;
   }

   /**
     *  批量删除
    * @param ids
    * @return
    */
   @DeleteMapping(value = "/deleteBatch")
   public Result<SysJobsMsgSend> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       Result<SysJobsMsgSend> result = new Result<>();
       if(ids==null || "".equals(ids.trim())) {
           result.error500("参数不识别！");
       }else {
           this.sysJobsMsgSendService.removeByIds(Arrays.asList(ids.split(",")));
           result.success("删除成功!");
       }
       return result;
   }

   /**
     * 通过id查询
    * @param id
    * @return
    */
   @GetMapping(value = "/queryById")
   public Result<SysJobsMsgSend> queryById(@RequestParam(name="id",required=true) String id) {
       Result<SysJobsMsgSend> result = new Result<>();
       SysJobsMsgSend old = sysJobsMsgSendService.getById(id);
       if(old==null) {
           result.error500("未找到对应实体");
       }else {
           result.setResult(old);
           result.setSuccess(true);
       }
       return result;
   }

   /**
    * @功能：更新用户系统消息阅读状态
    * @param json
    * @return
    */
   @PutMapping(value = "/editByAnntIdAndUserId")
   public Result<SysJobsMsgSend> editById(@RequestBody JSONObject json) {
       Result<SysJobsMsgSend> result = new Result<>();
       String anntId = json.getString("anntId");
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       LambdaUpdateWrapper<SysJobsMsgSend> updateWrapper = new UpdateWrapper().lambda();
       updateWrapper.set(SysJobsMsgSend::getReadFlag, CommonConstant.MSG_HAS_READ_FLAG);
       updateWrapper.set(SysJobsMsgSend::getReadTime, new Date());
       updateWrapper.last("where annt_id ='"+anntId+"' and user_id ='"+userId+"'");
       SysJobsMsgSend sysJobsMsgSend = new SysJobsMsgSend();
       sysJobsMsgSendService.update(sysJobsMsgSend, updateWrapper);
       result.setSuccess(true);
       return result;
   }

   /**
    * @功能：获取我的消息
    * @return
    */
   @GetMapping(value = "/getMyMsgSend")
   public Result<IPage<JobsMsgSendModel>> getMyMsgSend(JobsMsgSendModel jobsMsgSendModel,
                                                                @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize) {
       Result<IPage<JobsMsgSendModel>> result = new Result<>();
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       jobsMsgSendModel.setUserId(userId);
       jobsMsgSendModel.setPageNo((pageNo-1)*pageSize);
       jobsMsgSendModel.setPageSize(pageSize);
       Page<JobsMsgSendModel> pageList = new Page<>(pageNo,pageSize);
       pageList = sysJobsMsgSendService.getMyMsgSendPage(pageList, jobsMsgSendModel);
       result.setResult(pageList);
       result.setSuccess(true);
       return result;
   }

    /**
     * @功能：一键接收
     * @return
     */
    @PutMapping(value = "/receiveAll")
    public Result<SysJobsMsgSend> receiveAll() {
        Result<SysJobsMsgSend> result = new Result<>();
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        String userId = sysUser.getId();
        LambdaUpdateWrapper<SysJobsMsgSend> updateWrapper = new UpdateWrapper().lambda();
        updateWrapper.set(SysJobsMsgSend::getReadFlag, CommonConstant.MSG_NO_READ_FLAG);
        updateWrapper.set(SysJobsMsgSend::getReadTime, new Date());
        updateWrapper.last("where user_id ='"+userId+"'");
        SysJobsMsgSend sysJobsMsgSend= new SysJobsMsgSend();
        sysJobsMsgSendService.update(sysJobsMsgSend, updateWrapper);
        result.setSuccess(true);
        result.setMessage("全部已接收");
        return result;
    }

   /**
    * @功能：一键已读
    * @return
    */
   @PutMapping(value = "/readAll")
   public Result<SysJobsMsgSend> readAll() {
       Result<SysJobsMsgSend> result = new Result<>();
       LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
       String userId = sysUser.getId();
       LambdaUpdateWrapper<SysJobsMsgSend> updateWrapper = new UpdateWrapper().lambda();
       updateWrapper.set(SysJobsMsgSend::getReadFlag, CommonConstant.MSG_HAS_READ_FLAG);
       updateWrapper.set(SysJobsMsgSend::getReadTime, new Date());
       updateWrapper.last("where user_id ='"+userId+"'");
       SysJobsMsgSend sysJobsMsgSend= new SysJobsMsgSend();
       sysJobsMsgSendService.update(sysJobsMsgSend, updateWrapper);
       result.setSuccess(true);
       result.setMessage("全部已读");
       return result;
   }
}
