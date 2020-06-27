package com.es.example.dto;

import com.es.example.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * @author sy
 * @date Created in 2020.6.27 15:20
 * @description
 */
@Repository
public interface EmployeeRepository extends ElasticsearchRepository<Employee,String> {

    /**
     * 根据ID查询信息
     * @param id
     * @return
     */
    Employee queryEmployeeById(String id);

    /**
     *
     * @param firstName
     * @param lastName
     * @return
     */
    Page<Employee> findByFirstNameOrLastName(String firstName, String lastName, Pageable pageable);


    /**
     * 根据范围
     * @param age
     * @param age2
     * @return
     */
    Page<Employee> findByAgeBetween(double age, double age2, Pageable pageable);

}
