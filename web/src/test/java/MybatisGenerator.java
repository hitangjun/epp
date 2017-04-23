import com.baomidou.mybatisplus.annotations.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.ConfigGenerator;

/**
 * @author hurd@omeng.cc
 * @version v0.1
 * @date 2016/12/14
 * @desc mybatis 生成器
 */
public class MybatisGenerator {

    public static void main(String[] args) {
        ConfigGenerator cg = new ConfigGenerator();
        // 配置 MySQL 连接
        cg.setDbDriverName("com.mysql.jdbc.Driver");
        cg.setDbUrl("jdbc:mysql://192.168.1.248:3306/dbname?characterEncoding=utf8");
        cg.setDbUser("username");
        cg.setDbPassword("pwd");
        // 配置包名
        cg.setEntityPackage("com.hihexo.app.model");
        cg.setMapperPackage("com.hihexo.app.mapper");
        cg.setServicePackage("com.hihexo.app.service");
        cg.setControllerPackage("com.hihexo.app.controller");
        cg.setXmlPackage("mapping");
        cg.setServiceImplPackage("com.hihexo.app.service.impl");
        // 配置表主键策略
        cg.setIdType(IdType.AUTO);
        String os = System.getProperty("os.name");
        String path=  os.toLowerCase().contains("windows") ?"D://mq-gen": "/mnt/mq-gen/";
        // 配置保存路径
        cg.setSaveDir(path);
        // 其他参数请根据上面的参数说明自行配置，当所有配置完善后，运行AutoGenerator.run()方法生成Code
        // 生成代码
        AutoGenerator.run(cg);
    }

}
