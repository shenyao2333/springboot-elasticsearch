package com.es.example;

import com.alibaba.fastjson.JSON;
import com.es.example.dto.EmployeeRepository;
import com.es.example.entity.Employee;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class ExampleApplicationTests {


    @Resource
    private EmployeeRepository employeeRepository;

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;


    @Test
    void contextLoads() {
        Employee employee = new Employee();
        employee.setId("1");
        employee.setFirstName("测试的呀？");
        employee.setLastName("zh");
        employee.setAge(26);
        employee.setAbout("i am in peking");
        employeeRepository.save(employee);

    }

    @Test
    void testAddList() {
        ArrayList<Employee> employees = new ArrayList<>();

        for (int i = 1; i < 200; i++) {
            Employee employee = new Employee();
            employee.setId(i + 1 + "");
            employee.setFirstName("这不是真的" + i * 23);
            employee.setLastName("zh");
            employee.setAge(i * 2);
            employee.setAbout(UUID.randomUUID().toString());
            employees.add(employee);
        }

        employeeRepository.saveAll(employees);
    }


    void dsf() {
       /* // 构建查询内容
        QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder("12");
        // 查询的字段
        queryBuilder.field("title").field("content");

        Employee employee = new Employee();
        employee.setId("12");
        employee.setFirstName("这不是真的");
        employeeRepository.searchSimilar(employee,)
        Iterable<Answer> searchResult = employeeRepository.search(queryBuilder);
        Iterator<Answer> iterator = searchResult.iterator();
        List<Answer> list = new ArrayList<Answer>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;*/

    }


    @Test
    void testSearch() throws IOException {
     SearchRequest searchRequest = new SearchRequest("company");
     // 构建搜索条件      
     SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
     sourceBuilder.highlighter();
     // 查询条件，我们可以使用 QueryBuilders 工具来实
     // QueryBuilders.termQuery 精确  
     //QueryBuilders.matchAllQuery() 匹配所有
     TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("firstName", "沈瑶吗？");
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery("firstName", "真的207");
     sourceBuilder.query(termQueryBuilder);
     sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
     searchRequest.source(sourceBuilder);
     SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
     System.out.println(JSON.toJSONString(searchResponse.getHits()));
     System.out.println("=================================");
     for (SearchHit documentFields : searchResponse.getHits().getHits()) {
        System.out.println(documentFields.getSourceAsMap());
     }
    }



    @Test
    void testQuan() throws IOException {
        SearchRequest searchRequest = new SearchRequest("company");
        // 构建搜索条件      
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.from(0);
        sourceBuilder.size(10);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("firstName", "沈瑶吗？");
        //MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(termQueryBuilder);

        HighlightBuilder firstName = new HighlightBuilder().field("firstName");

        firstName.preTags("<span class = 'df'");
        firstName.postTags("</span>");
        sourceBuilder.highlighter(firstName);


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


    }



    /**
     * 查询遍历抽取
     * @param queryBuilder
     */
/*    private void searchFunction(QueryBuilder queryBuilder) {
        SearchResponse response = client.prepareSearch("twitter")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(100).execute().actionGet();

        while(true) {
            response = client.prepareSearchScroll(response.getScrollId())
                    .setScroll(new TimeValue(60000)).execute().actionGet();
            for (SearchHit hit : response.getHits()) {
                Iterator<Entry<String, Object>> iterator = hit.getSource().entrySet().iterator();
                while(iterator.hasNext()) {
                    Entry<String, Object> next = iterator.next();
                    System.out.println(next.getKey() + ": " + next.getValue());
                    if(response.getHits().hits().length == 0) {
                        break;
                    }
                }
            }
            break;
        }
//        testResponse(response);
    }*/


}
