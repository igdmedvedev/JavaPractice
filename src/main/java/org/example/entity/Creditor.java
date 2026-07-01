package org.example.entity;

import org.example.entity.common.Column;
import org.example.entity.common.IEntity;
import org.example.entity.common.NonEditColumn;
import org.example.entity.common.SequenceColumn;

import java.util.Map;

//Пример, если в БД есть таблица с тремя колонками
public class Creditor implements IEntity {
    @Column(title = "ID")
    @SequenceColumn
    @NonEditColumn
    private Integer id;

    @Column(title = "Name") private String name;
    @Column(title = "Age") private Integer age;

    public Creditor(Integer id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Creditor(Map<String, String> columns) {
        this(
            columns.get("id") != null ? Integer.valueOf(columns.get("id")) : null,
            columns.get("name"),
            Integer.valueOf(columns.get("age"))
        );
    }

    @Override
    public Integer getPk() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Integer getAge() {
        return age;
    }
}
