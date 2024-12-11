package org.example.backend;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;


public class CodeGenerator {

    @Test
    public void create() {
        String host = "localhost";
        String port = "3306";
        String database = "stackoverflow";
        String username = "root";
        String password = "mysql";
        String projectPath = "E:\\courses\\CS209A-ComputerSystemDesignAndApplicationsA\\project\\StackOverflow-Java-Analysis\\backend" + "/src/main/java";
        String author = "Yuxian Wu";
        String parent = "org.example";
        String moduleName = "backend";
        List<String> tables = List.of("answer", "comment", "question", "question_tag", "tag", "user");

        FastAutoGenerator.create("jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=GMT%2B8", username, password)
                .globalConfig(builder -> {
                    builder.author(author) // 设置作者
                            .outputDir(projectPath) // 输出目录
                            .disableOpenDir() // 生成后不打开文件夹
                            .enableSwagger();
//                            .dateType(DateType.ONLY_DATE); // 设置日期类型
                })
                .packageConfig(builder -> {
                    builder.moduleName(moduleName) // 设置模块名
                            .parent(parent) // 设置父包名
                            .controller("controller") // 设置 Controller 包名
                            .entity("entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper.xml"); // 设置 Mapper XML 文件包名
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables)// 设置需要生成的表名
                            .entityBuilder()
                            .enableRemoveIsPrefix() // 去除表前缀
                            .idType(IdType.ASSIGN_ID)
                            .enableLombok() // 启用 Lombok
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableChainModel()
                            .controllerBuilder()
                            .enableHyphenStyle()
                            .enableRestStyle() // 启用 REST 风格
                            .serviceBuilder()
                            .formatServiceFileName("%sService");
                })
                .execute(); // 执行生成
    }

//    @Test
//    public void run() {
//
//        // 1、创建代码生成器
//        AutoGenerator mpg = new AutoGenerator();
//
//        // 2、全局配置
//        GlobalConfig gc = new GlobalConfig();
//        String projectPath = System.getProperty("user.dir");
//        gc.setOutputDir(projectPath + "/src/main/java");
//        gc.setAuthor("testjava");
//        gc.setOpen(false); //生成后是否打开资源管理器
//        gc.setFileOverride(false); //重新生成时文件是否覆盖
//        gc.setServiceName("%sService");    //去掉Service接口的首字母I
//        gc.setIdType(IdType.ID_WORKER); //主键策略
//        gc.setDateType(DateType.ONLY_DATE);//定义生成的实体类中日期类型
//        gc.setSwagger2(true);//开启Swagger2模式
//
//        mpg.setGlobalConfig(gc);
//
//        // 3、数据源配置
//        DataSourceConfig dsc = new DataSourceConfig();
//        dsc.setUrl("jdbc:mysql://localhost:3306/guli");
//        dsc.setDriverName("com.mysql.jdbc.Driver");
//        dsc.setUsername("root");
//        dsc.setPassword("root");
//        dsc.setDbType(DbType.MYSQL);
//        mpg.setDataSource(dsc);
//
//        // 4、包配置
//        PackageConfig pc = new PackageConfig();
//        pc.setModuleName("edu"); //模块名
//        pc.setParent("com.example.demo");
//        pc.setController("controller");
//        pc.setEntity("entity");
//        pc.setService("service");
//        pc.setMapper("mapper");
//        mpg.setPackageInfo(pc);
//
//        // 5、策略配置
//        StrategyConfig strategy = new StrategyConfig();
//        strategy.setInclude("edu_teacher");
//        strategy.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        strategy.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//
//        strategy.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        strategy.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//
//        strategy.setRestControllerStyle(true); //restful api风格控制器
//        strategy.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//
//        mpg.setStrategy(strategy);
//
//
//        // 6、执行
//        mpg.execute();
//    }
}
