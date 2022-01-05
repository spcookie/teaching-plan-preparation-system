package com.cqut.tps.controller;

import com.cqut.tps.annotation.Api;
import com.cqut.tps.annotation.Para;
import com.cqut.tps.dto.MessageDto;
import com.cqut.tps.entity.BasicInformation;
import com.cqut.tps.entity.Curriculum;
import com.cqut.tps.server.InputServer;
import com.cqut.tps.server.InputServerBean;

import javax.servlet.annotation.*;
import java.util.List;
import java.util.Map;

/**
 * @author Augenstern
 * @date 2021/12/29
 */
@WebServlet(name = "InputController", value = "/input/*")
public class InputController extends BaseServlet {
    private final InputServer inputServerBean = new InputServerBean();

    @Api(path = "/inputBaseData")
    public MessageDto inputBaseInfo(BasicInformation basicInformation) {
        return inputServerBean.setBaseInfo(basicInformation);
    }

    @Api(path = "/inputDetailData")
    public Map<String, String> inputDetailData(List<Curriculum> curricula) {
       return inputServerBean.setDetailInfo(curricula);
    }

    @Api(path = "/inputShipData")
    public MessageDto inputShipData(List<List<String>> shipMap) {
        return inputServerBean.setShipInfo(shipMap);
    }

    @Api(path = "/generateTimetable")
    public Map<String, List<Curriculum>> generateTimetable(@Para(name = "strategy") Integer type) {
        return inputServerBean.generateTimetable(type);
    }
}
