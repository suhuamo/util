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
public class HttpTest {

    @Test
    public void sendGet() {
        // 请求地址
        String url = "http://www.baidu.com";
        // 获取返回值
        JsonObject jsonObject = HttpUtil.sendGet(url);
        System.out.println("jsonObject = " + jsonObject);
        // 拼接参数
        Map<String, String> params = new HashMap<>();
        params.put("name", "小花");
        params.put("age", "18");
        jsonObject = HttpUtil.sendGet(url, params);
        System.out.println("jsonObject = " + jsonObject);
        // 请求头参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Authoration", "token xxx123");
        jsonObject = HttpUtil.sendGet(url, params, headers);
        System.out.println("jsonObject = " + jsonObject);
    }

    // Put和Delete同理
    @Test
    public void sendPost() {
        // 请求地址
        String url = "http://www.baidu.com";
        // 请求数据
        Person person = new Person();
        // 获取返回值
        JsonObject jsonObject = HttpUtil.sendPost(url, person);
        System.out.println("jsonObject = " + jsonObject);
        // 请求头参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Authoration", "token xxx123");
        jsonObject = HttpUtil.sendPost(url, person, headers);
        System.out.println("jsonObject = " + jsonObject);
    }

    class Person {
        String name;
        Integer age;
    }
}
```
## 2.ExcelUtil
```java
public class ExcelTest {
    @Test
    public void readExcelTest() throws Exception {
        // 文件地址
        String file = "E:\\info\\test\\test.xlsx";
        // 读取文件数据
        List<Map<Integer, Object>> maps = ExcelUtil.readExcel(new File(file));
        maps.forEach(e -> {
            e.forEach((k, v) -> {
                System.out.println(k + ": " + v);
            });
        });
    }

    @Test
    public void writeExcelTest() throws Exception {
        // 文件地址
        String file = "E:\\info\\test\\write.xlsx";
        // 存入的数据
        List<List<Object>> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<Object> row = new ArrayList<>();
            for (int j = 0; j < 10; j++) {
                row.add(j);
            }
            data.add(row);
        }
        // 写入文件数据
        ExcelUtil.writeExcel(new File(file),data);
    }
}
```
## 3.FileUtil
```java
public class FileTest {
    @Test
    public void getResourceFilePathTest() throws ClassNotFoundException {
        String resourceFilePath = FileUtil.getResourceFilePath("t/2.txt");
        System.out.println("resourceFilePath = " + resourceFilePath);
    }

    @Test
    public void readFileTest() throws ClassNotFoundException, IOException {
        // 获取resource文件夹下的1.txt文件的绝对路径
        String resourceFilePath = FileUtil.getResourceFilePath("1.txt");
        // 读取文件的内容
        String s = FileUtil.readFile(resourceFilePath);
        System.out.println("s = " + s);
    }

    @Test
    public void writeFile() throws ClassNotFoundException, IOException {
        // 获取resource文件夹下的1.txt文件的绝对路径
        String resourceFilePath = FileUtil.getResourceFilePath("1.txt");
        String content = "你好呀\n欢迎使用FileUtil\nO(∩_∩)O";
        // 覆盖方式写入
        FileUtil.writeFile(resourceFilePath, content);
        // 追加方式写入
        FileUtil.writeFile(resourceFilePath, content, true);
    }
}

```