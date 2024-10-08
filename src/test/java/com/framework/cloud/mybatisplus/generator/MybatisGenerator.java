package com.framework.cloud.mybatisplus.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.enums.SqlLike;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.config.po.LikeTable;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.query.SQLQuery;
import com.framework.cloud.domain.model.BaseEntity;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.core.text.StrUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author youtao531 on 2024/10/3 18:16
 */
public class MybatisGenerator implements InitializingBean {

    private final String ds;
    /**
     * 配置文件路径
     */
    private final String cp;

    private final Map<String, String> properties = new HashMap<>();
    private String jdbcUrl;
    private String username;
    private String password;
    private String mainJavaPath;
    private String resourceXmlPath;

    public MybatisGenerator(String ds, String cp) {
        this.ds = ds;
        this.cp = cp;
    }

    @Override
    public void afterPropertiesSet() {
        File file = FileUtil.file(this.cp);
        List<String> strings = FileUtil.readLines(file, StandardCharsets.UTF_8);
        for (String string : strings) {
            properties.put(string.split("=")[0], string.split("=")[1]);
        }
        this.jdbcUrl = "jdbc:mysql://" + this.properties.get(ds + "_URL");
        this.username = this.properties.get(ds + "_USER");
        this.password = this.properties.get(ds + "_PASS");
    }

    /**
     * @param packageName       包名(例如：com.cloud.biz.kernel/com.cloud.biz.vendors)
     * @param bizPrefix         业务名称(例如：customer/message/order等)
     * @param tableLeft         表前缀(例如：t_)
     * @param superClass        基类
     * @param includeTableNames 包含的表名(不传值则生成`moduleName`对应的所有表)
     */
    public void generateTable(String packageName, String bizPrefix, String tableLeft, @Nullable Class<?> superClass, String... includeTableNames) {
        File file = FileUtil.file(this.cp);
        String projectPath = file.getParent();
        this.mainJavaPath = projectPath + "/src/main/java";

        String packagePath = StrUtil.replace(packageName, ".", "/");
        this.resourceXmlPath = projectPath + "/src/main/java/%s/%s/domain/mapper".formatted(packagePath, bizPrefix);

        DataSourceConfig.Builder databaseQueryBuilder = new DataSourceConfig
                .Builder(jdbcUrl, username, password)
                .databaseQueryClass(SQLQuery.class);
        FastAutoGenerator
                .create(databaseQueryBuilder)
                .globalConfig(builder -> builder
                        .author("Yt")
                        .disableOpenDir()
                        .enableSpringdoc()
                        .commentDate("yyyy/MM/dd")   //类注释时间
                        .dateType(DateType.TIME_PACK)       //使用Java8开始支持的时间类型
                        .outputDir(mainJavaPath)//类路径
                )
                .packageConfig(builder ->
                        builder
                                .parent(packageName)
                                .moduleName(bizPrefix)
                                .entity("domain.entity")
                                .service("domain.service")
                                .serviceImpl("domain.service.impl")
                                .mapper("domain.mapper")
                                .controller("interfaces.web")
                                .pathInfo(Collections.singletonMap(OutputFile.xml, resourceXmlPath))//Xml路径
                )
                .strategyConfig(builder -> {
                            builder
                                    .addInclude(includeTableNames)//具体的表名
                                    .likeTable(new LikeTable(null == tableLeft ? bizPrefix + "_" : tableLeft, SqlLike.RIGHT));

                            //////////////////Entity.java策略配置////////////////
                            if (null != superClass) {
                                builder
                                        .entityBuilder()
                                        .idType(IdType.AUTO)
                                        .enableLombok()
                                        .disableSerialVersionUID()
                                        .enableChainModel()
                                        .enableFileOverride() //重新生成覆盖旧文件
                                        .enableTableFieldAnnotation()//属性加上注解说明
                                        .naming(NamingStrategy.underline_to_camel)
                                        .columnNaming(NamingStrategy.underline_to_camel)
                                        .logicDeleteColumnName("deleted")
                                        .superClass(BaseEntity.class);
                            } else {
                                builder
                                        .entityBuilder()
                                        .idType(IdType.AUTO)
                                        .enableLombok()
                                        .disableSerialVersionUID()
                                        .enableChainModel()
                                        .enableFileOverride() //重新生成覆盖旧文件
                                        .enableTableFieldAnnotation()//属性加上注解说明
                                        .naming(NamingStrategy.underline_to_camel)
                                        .columnNaming(NamingStrategy.underline_to_camel)
                                        .logicDeleteColumnName("deleted");
                            }

                            //////////////////Service.java策略配置////////////////
                            builder
                                    .serviceBuilder()
                                    .formatServiceFileName("%sService");

                            //////////////////Mapper策略配置////////////////
                            builder
                                    .mapperBuilder()
                                    .formatMapperFileName("%sMapper")
                                    .formatXmlFileName("%sMapper");

                            //////////////////Controller策略配置////////////////
                            builder
                                    .controllerBuilder()
                                    .disable()
                                    .enableRestStyle()
                                    .formatFileName("%sController");
                        }
                )
                .templateConfig(builder ->
                        builder
                                .disable(TemplateType.CONTROLLER)
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
