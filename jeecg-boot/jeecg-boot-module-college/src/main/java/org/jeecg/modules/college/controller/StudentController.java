package org.jeecg.modules.college.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.college.entity.Major;
import org.jeecg.modules.college.entity.Student;
import org.jeecg.modules.college.service.StudentService;
import org.jeecg.modules.college.util.CollegeUtil;
import org.jeecg.modules.college.vo.MajorVo;
import org.jeecg.modules.college.vo.StudentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @version V1.0
 * @created with IntelliJ IDEA.
 * @Title: entity
 * @author: shumlinmeng
 * @description:
 */
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentService studentService;


    /**
     * 获取列表数据
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<StudentVo>> queryPageList(@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<StudentVo>> result = new Result<>();
        IPage<StudentVo> page = new Page<>(pageNo, pageSize);
        Map<String,Object> query = CollegeUtil.getParameterMap(req);
        query.put("pageNo",pageNo);
        query.put("pageSize",pageSize);
        List<StudentVo> pageList = studentService.queryList(query);
        long count = studentService.countList(query);
        page.setRecords(pageList);
        page.setTotal(count);
        result.setResult(page);
        result.setSuccess(true);
        return result;
    }

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<Student> add(@RequestBody Student student) {
        Result<Student> result = new Result<>();

        LambdaQueryWrapper<Student> query = new LambdaQueryWrapper<>();

        Student stu = studentService.getOne(query.eq(Student::getStudentCode,student.getStudentCode()).eq(Student::getBunchId,student.getBunchId()));

        if(stu != null){
            return result.error500("该学生编号已存在!");
        }
        try {
            studentService.saveStudent(student);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     *  编辑
     * @param student
     * @return
     */
    @PutMapping(value = "/edit")
    public Result<Student> edit(@RequestBody Student student) {
        Result<Student> result = new Result<>();
        Student old = studentService.getById(student.getId());
        if(old==null) {
            result.error500("未找到对应实体");
        }
        try {
            studentService.updateStudent(student);
            result.success("修改成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }
}
