package org.javaboy.vhr.service;

import org.javaboy.vhr.model.entity.Employee;
import org.javaboy.vhr.model.vo.RespPageBean;

import java.util.Date;

public interface EmployeeService {
    RespPageBean getEmployeeByPage(Integer page, Integer size, Employee employee, Date[] beginDateScope);

    Integer addEmp(Employee employee);
}
