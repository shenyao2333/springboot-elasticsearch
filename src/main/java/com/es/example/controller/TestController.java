package com.es.example.controller;

import com.alibaba.fastjson.JSON;
import com.es.example.dto.EmployeeRepository;
import com.es.example.entity.Employee;
import com.es.example.service.EsService;
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

    @Resource
    private EsService esService;

    @GetMapping("/add")
    public String add(){
        esService.addData();
        return "success";
    }

    @GetMapping("/addList")
    public void get(){
        esService.addListData();
    }




    /**
     *  根据id删除
     * @return
     */
    @GetMapping("deleteById")
    public String deleteById(){
        employeeRepository.deleteById("ISxi9XIB9BoTM5NXy5UB");
        return "删除成功。";
    }

    /**
     * 局部更新，默认是根据id来更新的，如果没id的话就是增加了。
     * @return
     */
    @GetMapping("update")
    public String update() {
        Employee employee = new Employee();
        employee.setFirstName("哈哈");
        employee.setLastName("zh");
        employeeRepository.save(employee);
        return "success";
    }


    /**
     * id查询
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
    public Page<Employee> getListByParam(String param){
        return  esService.getListByParam(param);
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


    /**
     * 使用构建对象的方式查询数据
     * @return
     * @throws IOException
     */
    @GetMapping("test")
    public Object saf() throws IOException{
        SearchRequest searchRequest = new SearchRequest("test_index");
        // SearchSourceBuilder 是构建搜索条件的盒子。
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //设置高亮的字段，也可以不设置。
        HighlightBuilder firstName = new HighlightBuilder().field("firstName");
        firstName.preTags("<span class = 'df'>");
        firstName.postTags("</span>");
        sourceBuilder.highlighter(firstName);

        //设置分页。
        sourceBuilder.from(0);
        sourceBuilder.size(10);


        /**
         * 常用的查询方式有：
         * .matchQuery() 这个是模糊查询
         * .matchAllQuery() 查询全部
         * .termQuery() 精确查询。
         */
        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("firstName", "小明");

        //设置响应超时时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);


        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //这个就是查询到的数据。
        //searchResponse.getHits().getHits();
        ArrayList<Map<String, Object>> maps = new ArrayList<>();
        for (SearchHit hit: searchResponse.getHits().getHits()){
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("firstName");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title!=null){
                StringBuffer sb = new StringBuffer();
                Text[] fragments = title.fragments();
                for (Text s : fragments){
                    sb.append(s);
                }
                sourceAsMap.put("firstName",sb);
            }
            maps.add(sourceAsMap);
        }
        return maps;
    }

}
