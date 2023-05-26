# util
自定义的工具类，不需要进行任何配置，只需要在pom中加入依赖即可使用，为了减轻各个项目重复造轮子的问题，开始编写该项目。
# 系统需求
jdk 1.8
# 当前版本
v1.0.0
# 使用教程
加入以下依赖即可
```
        <dependency>
            <groupId>com.suhuamo</groupId>
            <artifactId>util</artifactId>
            <version>${suhuamo.util-version}</version>
        </dependency>
```
# 系统介绍
## 1.HttpUtil
可进行 Http进行请求
```java
public void http()
{
    String url = "http://www.baidu.com";
//    获取请求结果，post，delete同样，且后面可以加参数
    JsonObject jsonObject = HttpUtil.sendGet(url);
}
```
## 2.ExcelUtil
```java
public void excel()
{
        String file = "E:\\info\\test\\test.xlsx";
        // 接受到excel的数据
        List<Map<Integer, Object>> maps = ExcelUtil.readExcel(new File(file));
        maps.forEach(e -> {
            e.forEach((k, v) -> {
                System.out.println(k + ": " + v);
            });
        });
}
```