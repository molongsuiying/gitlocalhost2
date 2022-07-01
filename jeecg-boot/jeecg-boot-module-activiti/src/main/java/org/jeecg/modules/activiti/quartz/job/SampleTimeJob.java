package org.jeecg.modules.activiti.quartz.job;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricIdentityLink;
import org.activiti.engine.history.HistoricProcessInstanceQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.ComboModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.activiti.entity.ActBusiness;
import org.jeecg.modules.activiti.entity.ActZprocess;
import org.jeecg.modules.activiti.entity.ActivitiConstant;
import org.jeecg.modules.activiti.entity.ProcessInsVo;
import org.jeecg.modules.activiti.service.Impl.ActBusinessServiceImpl;
import org.jeecg.modules.activiti.service.Impl.ActZprocessServiceImpl;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SampleTimeJob implements Job {


	@Autowired
	private TaskService taskService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	ActZprocessServiceImpl actZprocessService;

	@Autowired
	private ActBusinessServiceImpl actBusinessService;

	@Autowired
	ISysBaseAPI sysBaseAPI;


	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		//逾期日期
		int due = ActivitiConstant.PROCESS_DUE_TIME;

		//备注
		String comment1 = "您有一个任务已逾期,请及时处理";
		String comment2 = "您有一个任务即将逾期,请及时处理";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//当前日期
		Date now = new Date();


		List<ProcessInsVo> list = new ArrayList<>();
		//待办总量
		ProcessInstanceQuery InstQuery = runtimeService.createProcessInstanceQuery()
				.orderByProcessInstanceId().desc();
		List<ProcessInstance> processInstanceList = InstQuery.list();
		processInstanceList.forEach(e -> {
			list.add(new ProcessInsVo(e));
		});

		LoginUser user = sysBaseAPI.getUserByName("admin");
		list.forEach(e -> {

			// 关联当前任务
			List<Task> taskList = taskService.createTaskQuery().processInstanceId(e.getProcInstId()).list();
			if(taskList!=null&& taskList.size() > 0 ){
				Task currentTask = taskList.get(0);

				ActBusiness actBusiness = actBusinessService.getById(e.getBusinessKey());

				//候选用户
				List<String> candidate_userIds = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_candidate, currentTask.getId());
				//已同意用户
				List<String> userIds_p = actBusinessService.findUserIdListByTypeAndTaskId(ActivitiConstant.EXECUTOR_TYPE_p, currentTask.getId());

				//去除已同意用户
				candidate_userIds.removeAll(userIds_p);

				if(candidate_userIds.size()>0){
					//判断是否已经有截止日期
					if(currentTask.getDueDate() == null){
						Date taskTime = currentTask.getCreateTime();

						//当前任务创建时间小于当前时间
						if(taskTime.getTime() < now.getTime()){
							Calendar cal = Calendar.getInstance();
							cal.setTime(taskTime);
							cal.add(Calendar.HOUR, due);// 24小时制
							Date dueTime = cal.getTime();
							//判断 due 小时后 时间 若dueTime
							if(dueTime.getTime() > now.getTime()){
								for (int i = 0; i < candidate_userIds.size(); i++) {
									String assignee = candidate_userIds.get(i);
									if(StringUtils.isNotBlank(assignee)){
										actZprocessService.sendUrgentMessage(user,sysBaseAPI.getUserByName(assignee),
												actBusiness,currentTask.getName(),format.format(taskTime),comment1,true, false, false);
									}
								}
							}
						}
					}else{
						Date dueDate = currentTask.getDueDate();
						//当前截止日期时间小于当前时间 已逾期
						if(dueDate.getTime()<now.getTime()){
							for (int i = 0; i < candidate_userIds.size(); i++) {
								String assignee = candidate_userIds.get(i);
								if(StringUtils.isNotBlank(assignee)){
									actZprocessService.sendUrgentMessage(user,sysBaseAPI.getUserByName(assignee),
											actBusiness,currentTask.getName(),format.format(dueDate),comment1,true, false, false);
								}
							}
						}else{
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.HOUR, due);// 24小时制
							Date dueTime = cal.getTime();
							//判断 当前时间 due 小时后 时间 dueDate < dueTime 则即将逾期
							if(dueDate.getTime() < dueTime.getTime()){
								for (int i = 0; i < candidate_userIds.size(); i++) {
									String assignee = candidate_userIds.get(i);
									if(StringUtils.isNotBlank(assignee)){
										actZprocessService.sendUrgentMessage(user,sysBaseAPI.getUserByName(assignee),
												actBusiness,currentTask.getName(),format.format(dueDate),comment2,true, false, false);
									}

								}
							}
						}
					}
				}

			}


		});

	}

}
