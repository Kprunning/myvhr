package org.javaboy.vhr.service.impl;

import org.javaboy.vhr.mapper.EmployeeMapper;
import org.javaboy.vhr.model.Employee;
import org.javaboy.vhr.model.RespPageBean;
import org.javaboy.vhr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public RespPageBean getEmployeeByPage(Integer page, Integer size, Employee employee, Date[] beginDateScope) {
        int startIndex;
        if (page != null && size != null) {
            // 标识开始位置,用于分页查询select ... limit page,size,查询page+1到page+size的记录
            page = (page - 1) * size;
        }
        List<Employee> data = employeeMapper.getEmployeeByPage(page, size, employee, beginDateScope);
        // 查询到的总记录数
        Long total = employeeMapper.getTotal(employee, beginDateScope);
        RespPageBean respPageBean = new RespPageBean();
        respPageBean.setTotal(total);
        respPageBean.setData(data);
        return respPageBean;
    }
}
