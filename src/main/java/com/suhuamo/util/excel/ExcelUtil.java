package com.suhuamo.util.excel;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author suhuamo
 * @slogan 想和喜欢的人睡在冬日的暖阳里
 * @date 2023/05/17
 * excel处理工具
 */
public class ExcelUtil {
    /**
     * 缓存读取excel行数
     */
    private final static Integer ROW_CACHE_SIZE = 100;
    /**
     * 读取资源时，缓存到内存的字节大小
     */
    private final static Integer BUFFER_SIZE = 1024 * 100;
    /**
     * 读取资源时，缓存到内存的字节大小,从0开始
     */
    private final static Integer SHEET_INDEX = 0;
    /**
     * 创建Excel的默认sheet名称
     */
    private final static String SHEET_NAME = "Sheet1";

    /**
     * 读取excel中的数据,读取该文件中的所有行的数据，并且读取每一列的信息,每一列的列名映射对应为列数,空行不读取
     * 返回类型为List<Map<String, Object>>，每一行数据对应为一个map,map类型为<Integer,object>,对应属性为，列数(第几列)：单元格内容
     * 格式限制：必须使用xlsx的格式，调用前需判断格式
     *
     * @param file excel文件，格式必须为 xlsx格式
     * @return List<Map < String, Object>>
     */
    public static List<Map<Integer, Object>> readExcel(File file, Integer sheetIndex) throws Exception {
        return readBigExcel(file, null, sheetIndex);
    }

    /**
     * 读取excel中的数据,读取该文件中的所有行的数据，并且读取每一列的信息,每一列的列名映射对应为列数,空行不读取
     * 返回类型为List<Map<String, Object>>，每一行数据对应为一个map,map类型为<Integer,object>,对应属性为，列数(第几列)：单元格内容
     * 格式限制：必须使用xlsx的格式，调用前需判断格式
     *
     * @param inputStream excel文件，格式必须为 xlsx格式
     * @return List<Map < String, Object>>
     */
    public static List<Map<Integer, Object>> readExcel(InputStream inputStream, Integer sheetIndex) throws Exception {
        return readBigExcel(null, inputStream, sheetIndex);
    }

    /**
     * 读取excel中的数据,读取该文件中的所有行的数据，并且读取每一列的信息,每一列的列名映射对应为列数,空行不读取
     * 返回类型为List<Map<String, Object>>，每一行数据对应为一个map,map类型为<Integer,object>,对应属性为，列数(第几列)：单元格内容
     * 格式限制：必须使用xlsx的格式，调用前需判断格式
     *
     * @param file excel文件，格式必须为 xlsx格式
     * @return List<Map < String, Object>>
     */
    public static List<Map<Integer, Object>> readExcel(File file) throws Exception {
        return readBigExcel(file, null, SHEET_INDEX);
    }

    /**
     * 读取excel中的数据,读取该文件中的所有行的数据，并且读取每一列的信息,每一列的列名映射对应为列数,空行不读取
     * 返回类型为List<Map<String, Object>>，每一行数据对应为一个map,map类型为<Integer,object>,对应属性为，列数(第几列)：单元格内容
     * 格式限制：必须使用xlsx的格式，调用前需判断格式
     *
     * @param inputStream excel文件，格式必须为 xlsx格式
     * @return List<Map < String, Object>>
     */
    public static List<Map<Integer, Object>> readExcel(InputStream inputStream) throws Exception {
        return readBigExcel(null, inputStream, SHEET_INDEX);
    }

    /**
     * 读取excel中的数据,读取该文件中的所有行的数据，并且读取每一列的信息,每一列的列名映射对应为列数,空行不读取
     * 返回类型为List<Map<String, Object>>，每一行数据对应为一个map,map类型为<Integer,object>,对应属性为，列数(第几列)：单元格内容
     * 格式限制：必须使用xlsx的格式，调用前需判断格式
     *
     * @param file excel文件，格式必须为 xlsx格式
     * @return List<Map < String, Object>>
     */
    private static List<Map<Integer, Object>> readBigExcel(File file, InputStream inputStream, Integer sheetIndex) throws Exception {
        //定义返回值
        List<Map<Integer, Object>> resultList = new ArrayList<Map<Integer, Object>>();
        // 定义excel
        Workbook wk = null;
        StreamingReader.Builder builder = StreamingReader.builder()
                .rowCacheSize(ROW_CACHE_SIZE)//缓存到内存中的行数，默认是10
                .bufferSize(BUFFER_SIZE);//读取资源时，缓存到内存的字节大小，默认是1024
        // 获取文件输入流
        // 也可以使用 try-with-resources ，即格式为 try(资源定义) {操作}，可以省去 catch 和 finally，会自动关闭资源
        try {
            //打开资源，必须，可以是InputStream或者是File，注意：只能打开XLSX格式的文件
            if (file != null) {
                wk = builder.open(file);
            } else {
                wk = builder.open(inputStream);
            }
            // 读取工作簿，默认读取第0个工作簿
            Sheet sheet = wk.getSheetAt(sheetIndex);
            //定义单元格
            Cell cell = null;
            // 这里相当于读到哪个row了，就加载哪个row，所以是使用的缓存，所以在这一个for里面能遍历完所有的行，但是是每读了100(上面定义的100)个row就才开始又加载100row，但始终是在这个for里面加载的
            //获取当前循环的行数据(因为只缓存了部分数据，所以不能用getRow来获取)此处采用增强for循环直接获取row对象
            for (Row row : sheet) {
                // 设置当前这一行保存数据的对象，格式： 列数(第几列):内容
                Map<Integer, Object> paramMap = new HashMap<Integer, Object>();//定义一个map做数据接收
                // 如果该行是空行，那么无法使用任何函数，直接存读取下一行即可
                if (isEmpty(row)) {
                    continue;
                }
                // 如果该行有效，那么读取每一个单元格的数据
                for (int i = 0; i < row.getLastCellNum(); i++) {
                    //获取单元格数据
                    cell = row.getCell(i);
                    // 如果该单元格结果为空，那么直接在该格放入null值
                    if (isEmpty(cell)) {
                        //将单元格值放入map中
                        paramMap.put(i + 1, null);
                    } else {
                        //将单元格值放入map中
                        paramMap.put(i + 1, cell.getStringCellValue());
                    }
                }
                //一行循环完成，将该行的数据存入list
                resultList.add(paramMap);
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
        } finally {
            if (wk != null) {
                try {
                    wk.close(); // 注意要关闭Excel文档，释放资源
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 返回最终结果
        return resultList;
    }


    /**
     * 将数据按行写入到excel文件中
     *
     * @param file excel文件，格式可为 xlsx,xlx格式
     * @param data 写入的excel文件的完整数据，每一个List为一行数据
     * @return void
     */
    public static void writeExcel(File file, List<List<Object>> data) throws Exception {
        try (XSSFWorkbook wb = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {
            // 创建一个工作表
            XSSFSheet sheet = wb.createSheet(SHEET_NAME);
            // 创建自增数字标记行数
            AtomicInteger rowIdx = new AtomicInteger();
            // 遍历每一行
            data.stream().forEach(rowData -> {
                // 创建第idx行
                XSSFRow row = sheet.createRow(rowIdx.getAndIncrement());
                // 创建自增数字标记行数
                AtomicInteger cellIdx = new AtomicInteger();
                // 遍历每一格
                rowData.stream().forEach(cellData -> {
                    XSSFCell cell = row.createCell(cellIdx.getAndIncrement());
                    cell.setCellValue(String.valueOf(cellData));
                });
            });
            // 保存工作表
            wb.write(new FileOutputStream(file));
        }
    }

    /**
     * 判断当前这一行是否存在
     *
     * @param row
     * @return boolean
     */
    private static boolean isEmpty(Row row) {
        // 如果为空，则返回true
        if (row == null) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前这一个表格是否有数据
     *
     * @param cell
     * @return boolean
     */
    private static boolean isEmpty(Cell cell) {
        // 如果为空，则返回false
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return true;
        }
        return false;
    }
}
