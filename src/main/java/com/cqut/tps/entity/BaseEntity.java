package com.cqut.tps.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Augenstern
 * @date 2021/12/30
 */
@Entity(name = "基础实体类")
public class BaseEntity {
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Id
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id='" + id + '\'' +
                '}';
    }
}
