package com.cqut.tps.entity;

import javax.persistence.Entity;

/**
 * @author Augenstern
 * @date 2021/12/30
 */
@Entity(name = "基础课程信息")
public class BasicInformation extends BaseEntity {
    private Integer totalCourses;
    private Integer semester;
    private Integer limitCredits;

    public Integer getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Integer totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getLimitCredits() {
        return limitCredits;
    }

    public void setLimitCredits(Integer limitCredits) {
        this.limitCredits = limitCredits;
    }
}
