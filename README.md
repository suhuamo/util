# util
自定义的工具类，不需要进行任何配置，只需要在pom中加入依赖即可使用，为了减轻各个项目重复造轮子的问题，开始编写该项目。
# 系统需求
jdk 1.8
# 当前版本
v1.0.0
# 使用教程
第一种：
1. 下拉该项目或者下载源代码
2. 将此工程通过`mvn clean install`打包到本地仓库中。
3. 然后在你的项目中的pom.xml加入如下依赖
```
        <dependency>
            <groupId>com.suhuamo</groupId>
            <artifactId>util</artifactId>
        <!--        版本可自行配置，1.0.0版本一定可以运行-->
            <version>1.0.0</version>
        </dependency>
```
第二种：

本项目已经上传到了jitPack的中央仓库中，故用户可以直接通过maven下拉使用，pom.xml中的配置如下，
```
    <repositories>
        <!--        配置jitpack的远程仓库-->
        <repository>
            <id>jitpack.io</id>
            <url>https://www.jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.suhuamo</groupId>
            <artifactId>util</artifactId>
        <!--        版本可自行配置，1.0.0版本一定可以运行-->
            <version>1.0.0</version>
        </dependency>
    </dependencies>
```
# 功能介绍
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