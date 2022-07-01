package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourCertificate;

import java.util.List;
import java.util.Map;

public interface HonourCertificateMapper extends BaseMapper<HonourCertificate>{

    List<HonourCertificate> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourCertificate> queryListByIds(@Param("ids")List<String> ids);

    HonourCertificate findById(@Param("id")String id);
}
