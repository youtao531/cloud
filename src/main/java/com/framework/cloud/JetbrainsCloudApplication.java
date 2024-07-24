package com.framework.cloud;

import com.framework.cloud.spring.StartupTimeApplicationContextInitializer;
import org.dromara.hutool.core.io.file.FileUtil;
import org.dromara.hutool.extra.management.HostInfo;
import org.dromara.hutool.extra.management.ManagementUtil;
import org.dromara.hutool.extra.management.RuntimeInfo;
import org.dromara.hutool.extra.spring.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.TimeZone;

@EnableAsync
@EnableCaching
@EnableScheduling
@ServletComponentScan
@SpringBootApplication(scanBasePackages = "com.framework.cloud")
public class JetbrainsCloudApplication implements ApplicationListener<ApplicationReadyEvent> {

    static {
        if (0 != TimeZone.getDefault().getRawOffset()) {
            TimeZone utc = TimeZone.getTimeZone("UTC");
            TimeZone.setDefault(utc);
        }
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(JetbrainsCloudApplication.class);
        app.addInitializers(new StartupTimeApplicationContextInitializer());
        app.run(args);
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        initial(event);
    }

    /**
     * 初始化应用信息
     *
     * @param event {@link ApplicationReadyEvent}
     */
    @SuppressWarnings("all")
    public static void initial(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        BuildProperties buildProperties = SpringUtil.getBean(BuildProperties.class);
        HostInfo hostInfo = ManagementUtil.getHostInfo();
        RuntimeInfo runtimeInfo = ManagementUtil.getRuntimeInfo();

        //启动耗时
        long runMillis = event.getSpringApplication().getInitializers().stream()
                .filter(x -> x instanceof StartupTimeApplicationContextInitializer)
                .findFirst()
                .map(x -> ((StartupTimeApplicationContextInitializer) x).getMillis())
                .orElse(0L);

        String message = String.format("""
                        ----------------------------------------------------------------------------
                        \tServices: \t%s is running! Access URLs:
                        \tExternal: \thttp://%s:%s/doc.html
                        \tProfiles: \t%s
                        \tTimeZone: \t%s
                        \tMemories: \tmax: %s, total: %s, free: %s, Usable: %s
                        \tVersions: \t%s:%s:%s
                        \tBuilders: \t%s
                        \tRunTimes: \t%s ms
                        ----------------------------------------------------------------------------""",
                buildProperties.getName(),
                hostInfo.getAddress(), env.getProperty("server.port"),
                Arrays.toString(env.getActiveProfiles()),
                ZoneId.systemDefault(),
                FileUtil.readableFileSize(runtimeInfo.getMaxMemory()), FileUtil.readableFileSize(runtimeInfo.getTotalMemory()), FileUtil.readableFileSize(runtimeInfo.getFreeMemory()), FileUtil.readableFileSize(runtimeInfo.getUsableMemory()),
                buildProperties.getGroup(), buildProperties.getArtifact(), buildProperties.getVersion(),
                buildProperties.getTime().atZone(ZoneId.systemDefault()).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                runMillis);
        System.out.println(message);
    }
}