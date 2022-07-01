package org.jeecg.modules.system.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
//    scheduledExecutor.scheduleWithFixedDelay(new TimerTask() {
//        @Override
//        public void run() {
//            System.out.println("第一个定时器：" + format.format(new Date()));
//        }
//    }, 0, 2, TimeUnit.SECONDS);
//        scheduledExecutor.scheduleWithFixedDelay(new TimerTask() {
//        @Override
//        public void run() {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("第二个定时器：" + format.format(new Date()));
//        }
//    }, 0, 2, TimeUnit.SECONDS);
}
