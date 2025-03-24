package g06.ecnu.heartbridge.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {
    public static void main(String[] args) {
        String url = "jdbc:h2:file:./data/testdb";
        String username = "sa";  // 数据库用户名
        String password = "";  // 数据库密码
        String outputDir = System.getProperty("user.dir") + "/src/main/java";  // 代码输出路径

        // 需要生成的表
        List<String> tables = Arrays.asList("public.users", "admin", "schedule", "consultant");

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("Tennsai Minamoto") // 设置作者
                            .outputDir(outputDir) // 指定输出目录
                            .disableOpenDir(); // 生成完不自动打开目录
                })
                .packageConfig(builder -> {
                    builder.parent("g06.ecnu") // 设置包名
                            .moduleName("heartbridge") // 设置模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/resources/mapper")); // 仅生成 XML
                })
                .strategyConfig(builder -> {
                    builder.addInclude(tables) // 生成多个表
                            .addTablePrefix("t_") // 过滤表前缀，如 t_user -> user
                            .entityBuilder().enableLombok().idType(IdType.AUTO) // 生成实体类，并使用 Lombok
                            .mapperBuilder().enableBaseColumnList().enableBaseResultMap(); // 生成 Mapper
                })
                .templateConfig(builder -> {
                    builder.service(null) // 不生成 Service
                            .controller(null) // 不生成 Controller
                            .xml(null)
                            .serviceImpl(null);
                })
                .templateEngine(new VelocityTemplateEngine()) // 使用 Velocity 模板引擎
                .execute();
    }
}
