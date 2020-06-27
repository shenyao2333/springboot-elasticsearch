package com.es.example.entity;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author sy
 * @date Created in 2020.6.27 15:21
 * @description
 */
@Document(indexName = "test_index", shards = 1,replicas = 0)
@Data
public class Employee {

    /**
     * index 是否创建索引，默认创建
     */
    @Field(index = false,type = FieldType.Text)
    private String id;

    /**
     * analyzer 存入时是否使用分词
     * type 在es里的类型。不指定可自动
     * store 是否独立存储
     * searchAnalyzer 查询分词
     */
    @Field(analyzer = "ik_max_word",type = FieldType.Text,store = true,searchAnalyzer = "ik_max_word")
    private String firstName;

    @Field(analyzer = "ik_max_word",type = FieldType.Text,store = true,searchAnalyzer = "ik_max_word")
    private String lastName;

    @Field
    private Integer age = 0;

    @Field(analyzer = "ik_max_word",type = FieldType.Text,store = true,searchAnalyzer = "ik_max_word")
    private String about;



}
