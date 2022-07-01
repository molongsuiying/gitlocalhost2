package org.jeecg.modules.system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
@RestController
@RequestMapping("/timer")
public class TimerController {
//    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);

@RequestMapping( value = "/a",method = RequestMethod.GET)
    public void timer() {
    System.out.print("这是一个接口"+"===");
//        scheduledExecutor.scheduleWithFixedDelay(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("第一个定时器：" + format.format(new Date()));
//            }
//        }, 0, 2, TimeUnit.SECONDS);
//        scheduledExecutor.scheduleWithFixedDelay(new TimerTask() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println("第二个定时器：" + format.format(new Date()));
//            }
//        }, 0, 2, TimeUnit.SECONDS);
    }
}

