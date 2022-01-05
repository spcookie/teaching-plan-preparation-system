package com.cqut.tps.server;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.cqut.tps.entity.Curriculum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Augenstern
 * @date 2022/1/4
 */
public class ExportServerBean extends BaseSever implements ExportServer {

    private File createFile(String path) {
        File file = new File(path);
        File parentFile = file.getParentFile();
        boolean isMk = true;
        if (parentFile.exists()) {
            File[] files = parentFile.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
            parentFile.delete();
        }
        if (parentFile.mkdirs()) {
            try {
                isMk = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        if (!isMk) {
            return null;
        }
        return file;
    }

    private void excelWrite(String path, HashMap<String, List<Curriculum>> map) throws FileNotFoundException {
        File file = this.createFile(path);
        if (file != null) {
            FileOutputStream out = new FileOutputStream(file);
            ExcelWriter excelWriter = new ExcelWriter(out, ExcelTypeEnum.XLSX);
            Sheet sheet = new Sheet(1, 0);
            sheet.setSheetName("sheet1");
            Table table = new Table(1);
            List<List<String>> titles = new ArrayList<>();
            titles.add(Collections.singletonList("学期"));
            titles.add(Collections.singletonList("课程"));
            table.setHead(titles);
            List<List<String>> curricula = new ArrayList<>();
            map.forEach((k, v) -> {
                StringBuilder names = new StringBuilder();
                for (Curriculum curriculum : v) {
                    names.append(curriculum.getName()).append(", ");
                }
                names.delete(names.length() - 2, names.length());
                curricula.add(Arrays.asList(String.valueOf(Integer.parseInt(k) + 1), names.toString()));
            });
            excelWriter.write0(curricula, sheet, table);
            excelWriter.finish();
        }
    }

    @Override
    public String ExportExcel(String rootPath) {
        String fileName = "file" + File.separator + UUID.randomUUID() + ".xlsx";
        String path = rootPath + fileName;
        HashMap<String, List<Curriculum>> timeTable = super.getTimeTable();
        try {
            this.excelWrite(path, timeTable);
            return "/" + fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
