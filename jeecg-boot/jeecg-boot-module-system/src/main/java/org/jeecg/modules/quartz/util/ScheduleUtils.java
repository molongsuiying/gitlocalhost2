package org.jeecg.modules.quartz.util;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.quartz.entity.SysJobs;
import org.jeecg.modules.quartz.entity.SysJobsMsg;
import org.jeecg.modules.quartz.entity.SysJobsMsgSend;
import org.jeecg.modules.quartz.entity.SysJobsMsgTemplate;
import org.jeecg.modules.quartz.service.ISysJobsMsgSendService;
import org.jeecg.modules.quartz.service.ISysJobsMsgService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @ClassName: ScheduleUtils
 * @Description: 定时任务类
 * @since version 1.0
 */
@Slf4j
@Component
public class ScheduleUtils {

    private ScheduleUtils() {
    }

    @Resource
    private ISysJobsMsgService msgService;

    @Resource
    private ISysJobsMsgSendService sendService;

    private static ScheduleUtils scheduleUtils;


    /**
     * @Title: init
     * @Description: 初始化启动定时任务
     */
    @PostConstruct
    void init() {
        scheduleUtils = this;
        scheduleUtils.msgService = this.msgService;
        scheduleUtils.sendService = this.sendService;

    }

    // task集合
    private static final Map<String, Task> TASK_MANAGER = new HashMap<String, Task>();
    // 定时器线程池
    private static final ScheduledExecutorService EXECUTOR_POOL = Executors.newScheduledThreadPool(10);
    // 定时任务队列
    private static final BlockingQueue<Task> TASK_QUEUE = new LinkedBlockingQueue<>();


    // 静态初始化方法
    static {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Task task = TASK_QUEUE.take();
                        // 任务有效，则执行任务
                        if (task.isEffective()) {
                            task.execute();
                        }
                    } catch (Exception e) {
                        log.error("定时任务执行异常：", e);
                    }
                }
            }
        });
        executor.shutdown();
    }


    /**
     *
     * @Title: add
     * @Description: 添加定时任务
     * @param job
     * @throws Exception
     */
    public synchronized static void add(SysJobs job, WebSocket webSocket) throws Exception {
        cancel(job); // 终结执行中的任务
        Task task = new Task(TASK_QUEUE, EXECUTOR_POOL, job,webSocket,scheduleUtils.msgService,scheduleUtils.sendService);
        TASK_MANAGER.put(job.getId(), task);
        // 将任务加入队列
        TASK_QUEUE.put(task);
    }


    /**
     *
     * @Title: cancel
     * @Description: 取消任务
     * @param job
     */
    public synchronized static void cancel(SysJobs job) {
        if (job == null) {
            return;
        }
        String jobName = job.getId();
        if (jobName == null) {
            return;
        }
        Task task = TASK_MANAGER.get(jobName);
        if (task != null) {
            // 关闭任务，停止任务线程
            task.setEffective(false);
            ScheduledFuture<?> future = task.getFuture();
            if (future != null) {
                future.cancel(true);
            }
        }
        TASK_MANAGER.remove(jobName);
    }

    /**
     *
     * @ClassName: Task
     * @Description: 任务内部类
     * @author lixk
     * @date 2017年8月16日 下午7:38:44
     * @version [1.0, 2017年8月16日]
     * @since version 1.0
     */
    private static class Task {
        private BlockingQueue<Task> queue; // 任务队列
        private CronTrigger trigger; // cron触发器
        private ScheduledExecutorService executor; // 定时器线程池
        private Class<?> clazz; // 反射类名
        private Task self; // task对象自己
        private SysJobs jobs;
        private WebSocket webSocket;
        private ISysJobsMsgService msgService;
        private ISysJobsMsgSendService sendService;
        private ScheduledFuture<?> future; // task对象的future
        private boolean effective = true; // task对象状态


        public Task(BlockingQueue<Task> queue, ScheduledExecutorService executor,SysJobs jobs,WebSocket webSocket,ISysJobsMsgService msgService,ISysJobsMsgSendService sendService) throws Exception {
            this.queue = queue;
            this.executor = executor;
            this.jobs = jobs;
            this.trigger = new CronTrigger(jobs.getCronExpression());
            this.clazz = Class.forName(jobs.getJobClassName());
            this.self = this;
            this.webSocket = webSocket;
            this.msgService = msgService;
            this.sendService = sendService;
        }

        public void execute() throws Exception {
            Date now = new Date();
            long delay = trigger.next(now).getTime() - now.getTime(); // 等待时间

            this.future = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        SysJobsMsgTemplate template = jobs.getTemplate();
                        // 1.插入通告表记录
                        SysJobsMsg msg = msgService.saveMsgByTemp(template);
                        String message = JSONObject.toJSONString(msg);
                        JSONObject obj = new JSONObject();

                        if(template.getMsgType().equals(CommonConstant.MSG_TYPE_ALL)) {

                            webSocket.sendAllMessage(message);

                        }else {

                            // 2.插入用户通告阅读标记表记录
                            String userId = msg.getUserIds();
                            String[] userIds = userId.substring(0, (userId.length()-1)).split(",");
                            String anntId = msg.getId();
                            Date refDate = new Date();

                            obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                            obj.put(WebsocketConst.MSG_ID, msg.getId());
                            obj.put(WebsocketConst.MSG_TXT, message);
                            for(int i=0;i<userIds.length;i++) {
                                SysJobsMsgSend sysJobsMsgSend = new SysJobsMsgSend();
                                sysJobsMsgSend.setAnntId(anntId);
                                sysJobsMsgSend.setUserId(userIds[i]);
                                sysJobsMsgSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                                sysJobsMsgSend.setReadTime(refDate);
                                sendService.save(sysJobsMsgSend);

                                obj.put(WebsocketConst.MSG_USER_ID, userId);

                                webSocket.sendBasicOneMessage(userId, obj.toJSONString());
                            }
                        }


                    } catch (Exception e) {
                        log.error("定时任务执行异常：", e);
                    } finally{
                        // 把当前任务加入队列
                        try {
                            queue.put(self);
                        } catch (InterruptedException e) {
                            log.error("添加定时任务到队列异常：", e);
                        }
                    }
                }
            }, delay, TimeUnit.MILLISECONDS);

        }

        public ScheduledFuture<?> getFuture() {
            return future;
        }

        public boolean isEffective() {
            return effective;
        }

        public void setEffective(boolean effective) {
            this.effective = effective;
        }
    }
}

