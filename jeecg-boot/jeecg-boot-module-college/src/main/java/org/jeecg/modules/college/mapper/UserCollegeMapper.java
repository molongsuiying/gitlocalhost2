package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.jeecg.modules.college.entity.UserCollege;

public interface UserCollegeMapper extends BaseMapper<UserCollege>{

    void deleteByUIdAndCid(UserCollege college);
}
