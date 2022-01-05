package com.cqut.tps.server;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;

/**
 * @author Augenstern
 * @date 2022/1/4
 */
public interface ExportServer {
    String ExportExcel(String rootPath);
}
