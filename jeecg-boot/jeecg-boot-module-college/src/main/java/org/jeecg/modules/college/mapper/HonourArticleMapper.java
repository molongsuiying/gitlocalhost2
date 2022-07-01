package org.jeecg.modules.college.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.college.entity.HonourArticle;

import java.util.List;
import java.util.Map;

public interface HonourArticleMapper extends BaseMapper<HonourArticle>{

    List<HonourArticle> queryList(Map<String, Object> params);

    int countList(Map<String, Object> params);

    void passBatch(@Param("ids")List<String> ids);

    void submitBatch(@Param("ids")List<String> ids);

    List<HonourArticle> queryListByIds(@Param("ids")List<String> ids);

    HonourArticle findById(@Param("id")String id);
}
