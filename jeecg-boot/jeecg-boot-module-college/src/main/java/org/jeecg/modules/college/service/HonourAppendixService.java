package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.HonourAppendix;

import java.util.List;

public interface HonourAppendixService extends IService<HonourAppendix>{

    List<HonourAppendix> queryListById(List<String>ids);

}
