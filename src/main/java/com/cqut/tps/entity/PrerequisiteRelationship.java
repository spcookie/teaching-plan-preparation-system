package com.cqut.tps.entity;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * @author Augenstern
 * @date 2021/12/30
 */
@Entity(name = "课程先修关系")
public class PrerequisiteRelationship extends BaseEntity {
    private Curriculum first;
    private Curriculum end;

    @OneToOne(targetEntity = Curriculum.class)
    public Curriculum getFirst() {
        return first;
    }

    public void setFirst(Curriculum first) {
        this.first = first;
    }

    @OneToOne(targetEntity = Curriculum.class)
    public Curriculum getEnd() {
        return end;
    }

    public void setEnd(Curriculum end) {
        this.end = end;
    }
}
