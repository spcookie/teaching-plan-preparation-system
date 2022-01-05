package com.cqut.tps.server;

import com.cqut.tps.dto.MessageDto;
import com.cqut.tps.entity.BaseEntity;
import com.cqut.tps.entity.BasicInformation;
import com.cqut.tps.entity.Curriculum;
import com.cqut.tps.entity.PrerequisiteRelationship;
import com.cqut.tps.util.IdUtil;
import com.cqut.tps.util.graph.IllegalOrderedRingGraphException;
import com.cqut.tps.util.graph.TableDirectedGraph;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author Augenstern
 * @date 2021/12/29
 */
public class InputServerBean extends BaseSever implements InputServer {

    @Override
    public MessageDto setBaseInfo(BasicInformation basicInformation) {
        BasicInformation basic = super.getBasicInformation();
        basic.setTotalCourses(basicInformation.getTotalCourses());
        basic.setSemester(basicInformation.getSemester());
        basic.setLimitCredits(basicInformation.getLimitCredits());
        return new MessageDto("课程基础信息设置成功", true);
    }

    @Override
    public Map<String, String> setDetailInfo(List<Curriculum> curricula) {
        List<Curriculum> curriculums = super.getCurriculums();
        if (!curriculums.isEmpty()) {
            curriculums.clear();
        }
        AtomicInteger i = new AtomicInteger();
        Map<String, String> collect = curricula.stream().peek(curriculum -> {
            String id = IdUtil.generateID(i.getAndIncrement());
            curriculum.setId(id);
        }).collect(Collectors.toMap(Curriculum::getName, BaseEntity::getId));
        curriculums.addAll(curricula);
        return collect;
    }

    @Override
    public MessageDto setShipInfo(List<List<String>> ship) {
        List<PrerequisiteRelationship> prerequisiteRelationship = super.getPrerequisiteRelationship();
        if (!prerequisiteRelationship.isEmpty()) {
            prerequisiteRelationship.clear();
        }
        List<Curriculum> curriculums = super.getCurriculums();
        for (List<String> ids : ship) {
            PrerequisiteRelationship relationship = new PrerequisiteRelationship();
            int firstIndex = IdUtil.generateIndex(ids.get(0));
            int endIndex = IdUtil.generateIndex(ids.get(1));
            relationship.setFirst(curriculums.get(firstIndex));
            relationship.setEnd(curriculums.get(endIndex));
            prerequisiteRelationship.add(relationship);
        }
        return new MessageDto("课程关系添加成功", true);
    }

    @Override
    public Map<String, List<Curriculum>> generateTimetable(int type) {
        ArrayList<Curriculum> curricula;
        try {
            curricula = this.generateSequence();
        } catch (IllegalOrderedRingGraphException e) {
            throw new RuntimeException("有向环图，无法拓扑遍历");
        }
        return switch (type) {
            case 0 -> this.sectionWithFront(curricula);
            case 1 -> this.sectionWithUniformity(curricula);
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    private ArrayList<Curriculum> generateSequence() throws IllegalOrderedRingGraphException {
        List<Curriculum> curriculums = super.getCurriculums();
        Curriculum[] vertexes = curriculums.toArray(new Curriculum[0]);
        List<PrerequisiteRelationship> ships = super.getPrerequisiteRelationship();
        int[][] edges = new int[ships.size()][];
        int i = 0;
        for (PrerequisiteRelationship ship : ships) {
            int f = IdUtil.generateIndex(ship.getFirst().getId());
            int e = IdUtil.generateIndex(ship.getEnd().getId());
            edges[i++] = new int[]{f, e, 0};
        }
        TableDirectedGraph<Curriculum> graph = TableDirectedGraph.build(vertexes, edges);
        ArrayList<Curriculum> result = new ArrayList<>();
        graph.topologicalSort(result::add);
        return result;
    }

    private Map<String, List<Curriculum>> sectionWithFront(List<Curriculum> curricula) {
        BasicInformation basic = super.getBasicInformation();
        AtomicInteger count = new AtomicInteger(0), i = new AtomicInteger(0);
        HashMap<String, List<Curriculum>> map = super.getTimeTable();
        map.clear();
        for (Curriculum curriculum : curricula) {
            count.set(count.get() + curriculum.getCredit());
            if (count.get() > basic.getLimitCredits()) {
                i.incrementAndGet();
                count.set(curriculum.getCredit());
            }
            List<Curriculum> list = map.computeIfAbsent(String.valueOf(i.get()), k -> new ArrayList<>());
            list.add(curriculum);
        }
        return map;
    }

    private Map<String, List<Curriculum>> sectionWithUniformity(List<Curriculum> curricula) {
        BasicInformation basic = super.getBasicInformation();
        Integer semester = basic.getSemester();
        Integer totalCourses = basic.getTotalCourses();
        Integer limitCredits = basic.getLimitCredits();
        int max = totalCourses / semester;
        AtomicInteger mod = new AtomicInteger(totalCourses % semester);
        AtomicInteger count = new AtomicInteger(0), i = new AtomicInteger(0);
        AtomicInteger credit = new AtomicInteger(0);
        HashMap<String, List<Curriculum>> map = super.getTimeTable();
        map.clear();
        for (Curriculum curriculum : curricula) {
            credit.set(credit.get() + curriculum.getCredit());
            if (count.get() < max && credit.get() <= limitCredits) {
                this.addAndCalculateCredits(map, curriculum, i);
                count.getAndIncrement();
            } else if (credit.get() <= limitCredits && mod.getAndDecrement() > 0) {
                this.addAndCalculateCredits(map, curriculum, i);
                count.getAndIncrement();
            } else {
                i.getAndIncrement();
                this.addAndCalculateCredits(map, curriculum, i);
                count.set(1);
                credit.set(curriculum.getCredit());
            }
        }
        return map;
    }

    private void addAndCalculateCredits(HashMap<String, List<Curriculum>> map, Curriculum curriculum, AtomicInteger i) {
        List<Curriculum> list = map.computeIfAbsent(String.valueOf(i.get()), k -> new ArrayList<>());
        list.add(curriculum);
    }
}
