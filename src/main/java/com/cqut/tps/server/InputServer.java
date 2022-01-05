package com.cqut.tps.server;

import com.cqut.tps.dto.MessageDto;
import com.cqut.tps.entity.BasicInformation;
import com.cqut.tps.entity.Curriculum;

import java.util.List;
import java.util.Map;

/**
 * @author Augenstern
 * @date 2021/12/29
 */
public interface InputServer {
    MessageDto setBaseInfo(BasicInformation basicInformation);
    Map<String, String> setDetailInfo(List<Curriculum> curricula);
    MessageDto setShipInfo(List<List<String>> ship);
    Map<String, List<Curriculum>> generateTimetable(int type);
}
