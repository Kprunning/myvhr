package org.javaboy.vhr.service;

import org.javaboy.vhr.model.Employee;
import org.javaboy.vhr.model.RespPageBean;

import java.util.Date;

public interface EmployeeService {
    RespPageBean getEmployeeByPage(Integer page, Integer size, Employee employee, Date[] beginDateScope);
}
