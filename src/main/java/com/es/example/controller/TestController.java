package com.es.example.controller;

import com.alibaba.fastjson.JSON;
import com.es.example.dto.EmployeeRepository;
import com.es.example.entity.Employee;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author sy
 * @date Created in 2020.6.27 15:24
 * @description
 */
@RestController
public class TestController {

    @Resource
    private EmployeeRepository employeeRepository;

    @Resource
    private RestHighLevelClient client;

    @GetMapping("/add")
    public void add(){
        Employee employee = new Employee();
        //employee.setId("1");
        employee.setFirstName("沈瑶啊");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        Employee save = employeeRepository.save(employee);
    }


    @GetMapping("/addList")
    public void get(){
        Employee employee = new Employee();
        employee.setId("1");
        employee.setFirstName("沈瑶吗？");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);

        employee = new Employee();
        employee.setId("2");
        employee.setFirstName("小明吗？");
        employee.setLastName("小弟弟");
        employee.setAge(20);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);
    }


    /**
     * 删除
     * @return
     */
    @GetMapping("delete")
    public String delete() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employeeRepository.delete(employee);
        return "success";
    }


    @GetMapping("deleteById")
    public String deleteById(){
        employeeRepository.deleteById("1");
        return "删除成功。";
    }

    /**
     * 局部更新
     * @return
     */
    @GetMapping("update")
    public String update() {
        Employee employee = employeeRepository.queryEmployeeById("1");
        employee.setFirstName("哈哈");
        employeeRepository.save(employee);
        System.err.println("update a obj");
        return "success";
    }


    /**
     * 查询
     * @return
     */
    @GetMapping("query")
    public Employee query() {
        return employeeRepository.queryEmployeeById("1");
    }


    /**
     * 使用构造器查询，过时了
     * @return
     */
   /* @GetMapping("getList2")
    public Object testPageable(){

        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders.multiMatchQuery("真的","firstName","lastName");
        Iterable<Employee> search = employeeRepository.search(multiMatchQuery,PageRequest.of(0,10));
        return search;
    }*/


    /**
     * 查询，使用or条件
     * 这里是可以使用一个关键字，然后多字段去配置查询。
     * 然后在根据age降序
     *
     * @return
     */
    @GetMapping("getList4")
    public Object getList4(){
        Page<Employee> employees = employeeRepository.findByFirstNameOrLastName("沈瑶","zh",PageRequest.of(0,10, Sort.Direction.DESC, "age"));
        return employees;
    }


    /**
     * 查询使用范围
     * @return
     */
    @GetMapping("getListBetween")
    public Object between(){
        Page<Employee> byAgeBetween = employeeRepository.findByAgeBetween(0, 100,PageRequest.of(0,10));
        return byAgeBetween;
    }

    @GetMapping("test")
    public Object saf() throws IOException{
        SearchRequest searchRequest = new SearchRequest("test_index");
        // 构建搜索条件      
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        HighlightBuilder firstName = new HighlightBuilder().field("firstName");
        firstName.preTags("<span class = 'df'>");
        firstName.postTags("</span>");
        sourceBuilder.highlighter(firstName);

        sourceBuilder.from(0);
        sourceBuilder.size(10);
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("firstName", "小明");

        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        sourceBuilder.query(matchQueryBuilder);

        searchRequest.source(sourceBuilder);



        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse);
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (SearchHit hit: searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("firstName");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title!=null){
                String sb = "";
                Text[] fragments = title.fragments();
                for (Text s : fragments){
                    sb+=s;
                }
                sourceAsMap.put("firstName",sb);
            }

            maps.add(sourceAsMap);
        }
        System.out.println(maps);
        return maps;
    }

}
