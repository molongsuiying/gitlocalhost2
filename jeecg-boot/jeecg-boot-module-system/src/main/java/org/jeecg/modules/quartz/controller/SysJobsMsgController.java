package org.jeecg.modules.quartz.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.CommonSendStatus;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.quartz.service.ISysJobsMsgSendService;
import org.jeecg.modules.quartz.service.ISysJobsMsgService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @Title: Controller
 * @Description: 系统定时通告表
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version: V1.0
 */
@RestController
@RequestMapping("/sys/jobsMsg")
@Slf4j
public class SysJobsMsgController {
	@Autowired
	private ISysJobsMsgService sysJobsMsgService;
	@Autowired
	private ISysJobsMsgSendService sysJobsMsgSendService;
	@Resource
	private WebSocket webSocket;

	/**
	 * 分页列表查询
	 * @param sysJobsMsg
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysJobsMsg>> queryPageList(SysJobsMsg sysJobsMsg,
												   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												   HttpServletRequest req) {
		Result<IPage<SysJobsMsg>> result = new Result<>();
		sysJobsMsg.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
		QueryWrapper<SysJobsMsg> queryWrapper = new QueryWrapper<>(sysJobsMsg);
		Page<SysJobsMsg> page = new Page<>(pageNo,pageSize);
		//排序逻辑 处理
		String column = req.getParameter("column");
		String order = req.getParameter("order");
		if(oConvertUtils.isNotEmpty(column) && oConvertUtils.isNotEmpty(order)) {
			if("asc".equals(order)) {
				queryWrapper.orderByAsc(oConvertUtils.camelToUnderline(column));
			}else {
				queryWrapper.orderByDesc(oConvertUtils.camelToUnderline(column));
			}
		}
		IPage<SysJobsMsg> pageList = sysJobsMsgService.page(page, queryWrapper);
		log.info("查询当前页："+pageList.getCurrent());
		log.info("查询当前页数量："+pageList.getSize());
		log.info("查询结果数量："+pageList.getRecords().size());
		log.info("数据总数："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 *   添加
	 * @param sysJobsMsg
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysJobsMsg> add(@RequestBody SysJobsMsg sysJobsMsg) {
		Result<SysJobsMsg> result = new Result<SysJobsMsg>();
		try {
			sysJobsMsg.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
			sysJobsMsg.setSendStatus(CommonSendStatus.UNPUBLISHED_STATUS_0);//未发布
			sysJobsMsgService.saveMsg(sysJobsMsg);
			result.success("添加成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失败");
		}
		return result;
	}

	/**
	 *  编辑
	 * @param sysJobsMsg
	 * @return
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysJobsMsg> eidt(@RequestBody SysJobsMsg sysJobsMsg) {
		Result<SysJobsMsg> result = new Result<>();
		SysJobsMsg SysJobsMsgEntity = sysJobsMsgService.getById(sysJobsMsg.getId());
		if(SysJobsMsgEntity==null) {
			result.error500("未找到对应实体");
		}else {
			boolean ok = sysJobsMsgService.upDateMsg(sysJobsMsg);
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
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<SysJobsMsg> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysJobsMsg> result = new Result<>();
		SysJobsMsg sysJobsMsg = sysJobsMsgService.getById(id);
		if(sysJobsMsg==null) {
			result.error500("未找到对应实体");
		}else {
			sysJobsMsg.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
			boolean ok = sysJobsMsgService.updateById(sysJobsMsg);
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
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<SysJobsMsg> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysJobsMsg> result = new Result<>();
		if(ids==null || "".equals(ids.trim())) {
			result.error500("参数不识别！");
		}else {
			String[] id = ids.split(",");
			for(int i=0;i<id.length;i++) {
				SysJobsMsg sysJobsMsg = sysJobsMsgService.getById(id[i]);
				sysJobsMsg.setDelFlag(CommonConstant.DEL_FLAG_1.toString());
				sysJobsMsgService.updateById(sysJobsMsg);
			}
			result.success("删除成功!");
		}
		return result;
	}

	/**
	 * 通过id查询
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<SysJobsMsg> queryById(@RequestParam(name="id",required=true) String id) {
		Result<SysJobsMsg> result = new Result<>();
		SysJobsMsg SysJobsMsg = sysJobsMsgService.getById(id);
		if(SysJobsMsg==null) {
			result.error500("未找到对应实体");
		}else {
			result.setResult(SysJobsMsg);
			result.setSuccess(true);
		}
		return result;
	}

	/**
	 *	 更新发布操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReleaseData", method = RequestMethod.GET)
	public Result<SysJobsMsg> doReleaseData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysJobsMsg> result = new Result<>();
		SysJobsMsg sysJobsMsg = sysJobsMsgService.getById(id);
		if(sysJobsMsg==null) {
			result.error500("未找到对应实体");
		}else {
			sysJobsMsg.setSendStatus(CommonSendStatus.PUBLISHED_STATUS_1);//发布中
			sysJobsMsg.setSendTime(new Date());
			String currentUserName = JwtUtil.getUserNameByToken(request);
			sysJobsMsg.setSender(currentUserName);
			boolean ok = sysJobsMsgService.updateById(sysJobsMsg);
			if(ok) {
				result.success("该系统通知发布成功");
				if(sysJobsMsg.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					JSONObject obj = new JSONObject();
					obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, sysJobsMsg.getId());
					obj.put(WebsocketConst.MSG_TXT, sysJobsMsg.getTitile());
					webSocket.sendAllMessage(obj.toJSONString());
				}else {
					// 2.插入用户通告阅读标记表记录
					String userId = sysJobsMsg.getUserIds();
					String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
					String anntId = sysJobsMsg.getId();
					Date refDate = new Date();
					JSONObject obj = new JSONObject();
					obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
					obj.put(WebsocketConst.MSG_ID, sysJobsMsg.getId());
					obj.put(WebsocketConst.MSG_TXT, sysJobsMsg.getTitile());
					webSocket.sendMoreMessage(userIds, obj.toJSONString());
				}
			}
		}

		return result;
	}

	/**
	 *	 更新撤销操作
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/doReovkeData", method = RequestMethod.GET)
	public Result<SysJobsMsg> doReovkeData(@RequestParam(name="id",required=true) String id, HttpServletRequest request) {
		Result<SysJobsMsg> result = new Result<>();
		SysJobsMsg sysJobsMsg = sysJobsMsgService.getById(id);
		if(sysJobsMsg==null) {
			result.error500("未找到对应实体");
		}else {
			sysJobsMsg.setSendStatus(CommonSendStatus.REVOKE_STATUS_2);//撤销发布
			sysJobsMsg.setCancelTime(new Date());
			boolean ok = sysJobsMsgService.updateById(sysJobsMsg);
			if(ok) {
				result.success("该系统通知撤销成功");
			}
		}

		return result;
	}

	/**
	 * @功能：补充用户数据，并返回系统消息
	 * @return
	 */
	@RequestMapping(value = "/listByUser", method = RequestMethod.GET)
	public Result<Map<String,Object>> listByUser() {
		Result<Map<String,Object>> result = new Result<>();
		LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
		String userId = sysUser.getId();
		// 1.将系统消息补充到用户通告阅读标记表中
		Collection<String> anntIds = sysJobsMsgSendService.queryByUserId(userId);
		LambdaQueryWrapper<SysJobsMsg> querySaWrapper = new LambdaQueryWrapper<SysJobsMsg>();
		querySaWrapper.eq(SysJobsMsg::getMsgType,CommonConstant.MSG_TYPE_ALL); // 全部人员
		querySaWrapper.eq(SysJobsMsg::getDelFlag,CommonConstant.DEL_FLAG_0.toString());  // 未删除
		querySaWrapper.eq(SysJobsMsg::getSendStatus, CommonConstant.HAS_SEND); //已发布
		querySaWrapper.ge(SysJobsMsg::getEndTime, sysUser.getCreateTime()); //新注册用户不看结束通知
		if(anntIds!=null&&anntIds.size()>0) {
			querySaWrapper.notIn(SysJobsMsg::getId, anntIds);
		}
		List<SysJobsMsg> announcements = sysJobsMsgService.list(querySaWrapper);
		if(announcements.size()>0) {
			for(int i=0;i<announcements.size();i++) {
				SysJobsMsgSend announcementSend = new SysJobsMsgSend();
				announcementSend.setAnntId(announcements.get(i).getId());
				announcementSend.setUserId(userId);
				announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
				sysJobsMsgSendService.save(announcementSend);
			}
		}
		// 2.查询用户未读的系统消息
		Page<SysJobsMsg> anntMsgList = new Page<>(0,5);
		anntMsgList = sysJobsMsgService.querySysCementPageByUserId(anntMsgList,userId,"1");//通知公告消息
		Page<SysJobsMsg> sysMsgList = new Page<>(0,5);
		sysMsgList = sysJobsMsgService.querySysCementPageByUserId(sysMsgList,userId,"2");//系统消息
		Map<String,Object> sysMsgMap = new HashMap<String, Object>();
		sysMsgMap.put("sysMsgList", sysMsgList.getRecords());
		sysMsgMap.put("sysMsgTotal", sysMsgList.getTotal());
		sysMsgMap.put("anntMsgList", anntMsgList.getRecords());
		sysMsgMap.put("anntMsgTotal", anntMsgList.getTotal());
		result.setSuccess(true);
		result.setResult(sysMsgMap);
		return result;
	}


	/**
	 * 导出excel
	 *
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysJobsMsg SysJobsMsg,HttpServletRequest request) {
		// Step.1 组装查询条件
		QueryWrapper<SysJobsMsg> queryWrapper = QueryGenerator.initQueryWrapper(SysJobsMsg, request.getParameterMap());
		//Step.2 AutoPoi 导出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysJobsMsg> pageList = sysJobsMsgService.list(queryWrapper);
		//导出文件名称
		mv.addObject(NormalExcelConstants.FILE_NAME, "系统通告列表");
		mv.addObject(NormalExcelConstants.CLASS, SysJobsMsg.class);
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("系统通告列表数据", "导出人:"+user.getRealname(), "导出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * 通过excel导入数据
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 获取上传文件对象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<SysJobsMsg> listSysJobsMsgs = ExcelImportUtil.importExcel(file.getInputStream(), SysJobsMsg.class, params);
				for (SysJobsMsg SysJobsMsgExcel : listSysJobsMsgs) {
					if(SysJobsMsgExcel.getDelFlag()==null){
						SysJobsMsgExcel.setDelFlag(CommonConstant.DEL_FLAG_0.toString());
					}
					sysJobsMsgService.save(SysJobsMsgExcel);
				}
				return Result.ok("文件导入成功！数据行数：" + listSysJobsMsgs.size());
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("文件导入失败！");
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件导入失败！");
	}
	/**
	 *同步消息
	 * @param anntId
	 * @return
	 */
	@RequestMapping(value = "/syncNotic", method = RequestMethod.GET)
	public Result<SysJobsMsg> syncNotic(@RequestParam(name="anntId",required=false) String anntId, HttpServletRequest request) {
		Result<SysJobsMsg> result = new Result<SysJobsMsg>();
		JSONObject obj = new JSONObject();
		if(StringUtils.isNotBlank(anntId)){
			SysJobsMsg SysJobsMsg = sysJobsMsgService.getById(anntId);
			if(SysJobsMsg==null) {
				result.error500("未找到对应实体");
			}else {
				if(SysJobsMsg.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {
					obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
					obj.put(WebsocketConst.MSG_ID, SysJobsMsg.getId());
					obj.put(WebsocketConst.MSG_TXT, SysJobsMsg.getTitile());
					webSocket.sendAllMessage(obj.toJSONString());
				}else {
					// 2.插入用户通告阅读标记表记录
					String userId = SysJobsMsg.getUserIds();
					if(oConvertUtils.isNotEmpty(userId)){
						String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
						obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
						obj.put(WebsocketConst.MSG_ID, SysJobsMsg.getId());
						obj.put(WebsocketConst.MSG_TXT, SysJobsMsg.getTitile());
						webSocket.sendMoreMessage(userIds, obj.toJSONString());
					}
				}
			}
		}else{
			obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_TOPIC);
			obj.put(WebsocketConst.MSG_TXT, "批量设置已读");
			webSocket.sendAllMessage(obj.toJSONString());
		}
		return result;
	}
}
