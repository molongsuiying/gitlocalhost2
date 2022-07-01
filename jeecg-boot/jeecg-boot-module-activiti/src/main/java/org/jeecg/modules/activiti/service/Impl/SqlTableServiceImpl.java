package org.jeecg.modules.activiti.service.Impl;

import org.jeecg.modules.activiti.mapper.SqlTableMapper;
import org.jeecg.modules.activiti.service.SqlTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlTableServiceImpl implements SqlTableService {
    @Autowired
    private SqlTableMapper sqlTableMapper;
    @Override
    public List<String> getTable() {
        return sqlTableMapper.getTable();
    }
}
