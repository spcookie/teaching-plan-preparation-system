package com.cqut.tps.constant;

/**
 * @author Augenstern
 * @date 2021/12/31
 */
public enum SystemConstant {
    BASIC("BasicInformation"),
    CURRICULUMS("Curriculums"),
    RELATION_SHIP("PrerequisiteRelationship"),
    TIME_TABLE("timeTable");

    private final String val;

    SystemConstant(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
