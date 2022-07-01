package org.jeecg.modules.college.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.entity.Student;
import org.jeecg.modules.college.entity.StudentCollege;
import org.jeecg.modules.college.exception.ErrorException;
import org.jeecg.modules.college.mapper.BunchMapper;
import org.jeecg.modules.college.mapper.StudentCollegeMapper;
import org.jeecg.modules.college.mapper.StudentMapper;
import org.jeecg.modules.college.service.StudentService;
import org.jeecg.modules.college.vo.StudentVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService{

    @Resource
    private StudentCollegeMapper studentCollegeMapper;

    @Override
    public void saveStudent(Student student) {
        this.baseMapper.insert(student);
        StudentCollege sc = studentCollegeMapper.findOne(student.getBunchId());
        sc.setStudentId(student.getId());
        Date time = student.getAdmissionTime();
        sc.setAdmissionTime(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        sc.setCollegeSession(year);

        studentCollegeMapper.insert(sc);


    }

    @Override
    public void updateStudent(Student student) throws ErrorException {
        int count = this.baseMapper.duplicateCheck(student.getId(),student.getBunchId(),student.getStudentCode());
        if(count > 0){
            throw new ErrorException("存在重复的学生编号");
        }
        this.baseMapper.updateById(student);
        LambdaQueryWrapper<StudentCollege> query = new LambdaQueryWrapper<>();
        StudentCollege sc = studentCollegeMapper.selectOne(query.eq(StudentCollege::getStudentId,student.getId()));
        Date time = student.getAdmissionTime();
        sc.setAdmissionTime(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        sc.setCollegeSession(year);
        studentCollegeMapper.updateById(sc);
    }

    @Override
    public long countList(Map<String, Object> queryParam) {
        return this.baseMapper.countList(queryParam);
    }

    @Override
    public List<StudentVo> queryList(Map<String, Object> queryParam) {
        return this.baseMapper.queryList(queryParam);
    }
}
