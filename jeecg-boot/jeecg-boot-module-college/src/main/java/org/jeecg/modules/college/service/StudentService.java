package org.jeecg.modules.college.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.college.entity.Bunch;
import org.jeecg.modules.college.entity.Student;
import org.jeecg.modules.college.exception.ErrorException;
import org.jeecg.modules.college.vo.StudentVo;

import java.util.List;
import java.util.Map;

public interface StudentService extends IService<Student>{

    void saveStudent(Student student);

    void updateStudent(Student student) throws ErrorException;

    long countList(Map<String,Object> queryParam);

    List<StudentVo> queryList(Map<String,Object> queryParam);
}
