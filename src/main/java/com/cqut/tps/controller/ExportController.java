package com.cqut.tps.controller;

import com.cqut.tps.annotation.Api;
import com.cqut.tps.server.ExportServer;
import com.cqut.tps.server.ExportServerBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Augenstern
 * @date 2022/1/4
 */
@WebServlet(name = "ExportController", value = "/export/*")
public class ExportController extends BaseServlet {
    private final ExportServer exportServer = new ExportServerBean();
    private String webRootPath;

    @Override
    public void init() {
        this.webRootPath = getServletContext().getRealPath("/");
    }

    @Api(path = "/excel")
    public String exportExcel() {
        return exportServer.ExportExcel(webRootPath);
    }
}
