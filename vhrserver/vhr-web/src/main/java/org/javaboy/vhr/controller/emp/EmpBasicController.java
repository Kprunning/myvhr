package org.javaboy.vhr.controller.emp;

import org.javaboy.vhr.model.entity.Employee;
import org.javaboy.vhr.model.vo.RespBean;
import org.javaboy.vhr.model.vo.RespPageBean;
import org.javaboy.vhr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/employee/basic")
public class EmpBasicController {
    private EmployeeService employeeService;
    @Autowired
    public EmpBasicController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     *
     * @param page 查询页码
     * @param size 页面大小默认10
     * @param employee "高级搜索" 的员工信息,封装到员工实体类
     * @param beginDateScope "高级搜索"功能的"入职日期范围"
     * @return
     */
    @GetMapping("/")
    public RespPageBean getEmployeeByPage(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size, Employee employee, Date[] beginDateScope){
        return employeeService.getEmployeeByPage(page,size,employee,beginDateScope);
    }

    @PostMapping
    public RespBean addEmp(@RequestBody Employee employee) {
        if (employeeService.addEmp(employee) == 1) {
            return RespBean.ok("添加成功!");
        }
        return RespBean.error("添加失败!");
    }
}
