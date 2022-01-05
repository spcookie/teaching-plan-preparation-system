package com.cqut.tps.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Augenstern
 * @date 2021/12/29
 */
@Entity(name = "课程")
public class Curriculum extends BaseEntity {
    private String name;
    private Integer credit;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "Curriculum{" +
                "name='" + name + '\'' +
                ", credit=" + credit +
                "} " + super.toString();
    }
}
