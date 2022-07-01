package org.jeecg.modules.quartz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * @Description: 定时任务在线管理
 * @Author: jeecg-boot
 * @Date:  2019-01-02
 * @Version: V1.0
 */
@Data
@TableName("sys_jobs")
public class SysJobs implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**创建人*/
	private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private java.util.Date updateTime;
	/**任务类名*/
	@Excel(name="任务类名",width=40)
	private String jobClassName;
	/**cron表达式*/
	@Excel(name="cron表达式",width=30)
	private String cronExpression;
	/**参数*/
	@Excel(name="方法名",width=15)
	private String methodName;
	/**参数*/
	@Excel(name="模板ID",width=15)
	private String tempMsgId;
	/**描述*/
	@Excel(name="描述",width=40)
	private String description;
	/**状态 0正常 -1停止*/
	@Excel(name="状态",width=15,dicCode="quartz_status")
	@Dict(dicCode = "status")
	private Integer status;
	/**任务名*/
	@Excel(name="任务名",width=40)
	private String jobName;
	/**版本号*/
	@Excel(name="版本号",width=15,dicCode="jobVersion")
	private String jobVersion;

	@TableField(exist = false)
	private SysJobsMsgTemplate template;
}
