package org.javaboy.vhr.service.impl;

import org.javaboy.vhr.mapper.EmployeeMapper;
import org.javaboy.vhr.model.entity.Employee;
import org.javaboy.vhr.model.mail.MailConstants;
import org.javaboy.vhr.model.mail.MailSendLog;
import org.javaboy.vhr.model.vo.RespPageBean;
import org.javaboy.vhr.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private EmployeeMapper employeeMapper;

    public SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    public SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    public SimpleDateFormat decimalFormat = new SimpleDateFormat("##.00");


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
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

    @Override
    public Integer addEmp(Employee employee) {
        // 合同开始日期
        Date beginContract = employee.getBeginContract();
        // 合同结束日期
        Date endContract = employee.getEndContract();
        // 合同持续月份,这个计算方式有点麻烦哦
        double month = (Double.parseDouble(yearFormat.format(endContract)) - Double.parseDouble(yearFormat.format(beginContract))) * 12
                + (Double.parseDouble(monthFormat.format(endContract)) - Double.parseDouble(monthFormat.format(beginContract)));
        // 这一段计算合同时间的代码真的繁琐,后续能否自己优化?
        employee.setContractTerm(Double.parseDouble(decimalFormat.format(month / 12)));
        // 录入进过挑选的员工?
        int result = employeeMapper.insertSelective(employee);
        // 录入完成后,给员工发送入职邮件
        if (result == 1) {
            Employee emp = employeeMapper.getEmployeeById(employee.getId());
            // 生成消息唯一id
            String msgId = UUID.randomUUID().toString();
            MailSendLog mailSendLog = new MailSendLog();
            mailSendLog.setMsgId(msgId);
            mailSendLog.setCreateTime(new Date());
            mailSendLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
            mailSendLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
            mailSendLog.setEmpId(emp.getId());
            mailSendLog.setTryTime(new Date(System.currentTimeMillis() + 1000 * 60 * MailConstants.MSG_TIMEOUT));
           // mailSendLogService.
        }
        return 0;
    }
}
