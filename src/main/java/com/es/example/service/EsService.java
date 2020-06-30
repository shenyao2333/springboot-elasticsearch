package com.es.example.service;

import com.es.example.dto.EmployeeRepository;
import com.es.example.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author sy
 * @date Created in 2020.6.30 21:14
 * @description Es处理
 */
@Service
public class EsService {


    @Resource
    private EmployeeRepository employeeRepository;

    public void addData() {

        Employee employee = new Employee();
        employee.setId("1");
        employee.setFirstName("沈瑶啊");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);
    }

    public void addListData() {
        List<Employee> employees = new ArrayList<>();
        for (int i = 10; i < 30; i++) {
            Employee employee = new Employee();
            employee.setId(i+"");
            employee.setFirstName("超人"+i + "号");
            employee.setLastName("zh");
            employee.setAge(2*i);
            employee.setAbout(UUID.randomUUID().toString());
            employees.add(employee);
        }
        employeeRepository.saveAll(employees);
    }

    /**
     * 根据关键字查询数据
     *
     * PageRequest 分页。是从0开始
     * Sort.Direction.DESC 排序规则
     * @param param
     * @return
     */
    public Page<Employee> getListByParam(String param) {
        return employeeRepository.findByFirstNameOrLastName(param,param, PageRequest.of(0,10, Sort.Direction.DESC, "age"));
    }
}
