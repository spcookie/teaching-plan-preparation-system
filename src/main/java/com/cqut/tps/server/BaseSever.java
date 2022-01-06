package com.cqut.tps.server;

import com.cqut.tps.constant.SystemConstant;
import com.cqut.tps.entity.BasicInformation;
import com.cqut.tps.entity.Curriculum;
import com.cqut.tps.entity.PrerequisiteRelationship;
import com.cqut.tps.util.SessionUtil;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;

/**
 * @author Augenstern
 * @date 2021/12/30
 */
public abstract class BaseSever {

    public BaseSever() {
    }

    protected BasicInformation getBasicInformation() {
        HttpSession httpSession = SessionUtil.get();
        return (BasicInformation) httpSession.getAttribute(SystemConstant.BASIC.getVal());
    }

    protected List<Curriculum> getCurriculums() {
        HttpSession httpSession = SessionUtil.get();
        return (List<Curriculum>) httpSession.getAttribute(SystemConstant.CURRICULUMS.getVal());
    }

    protected List<PrerequisiteRelationship> getPrerequisiteRelationship() {
        HttpSession httpSession = SessionUtil.get();
        return (List<PrerequisiteRelationship>) httpSession.getAttribute(SystemConstant.RELATION_SHIP.getVal());
    }

    protected HashMap<String, List<Curriculum>> getTimeTable() {
        HttpSession httpSession = SessionUtil.get();
        return (HashMap<String, List<Curriculum>>) httpSession.getAttribute(SystemConstant.TIME_TABLE.getVal());
    }
}
