package top.xym.web.rag.entity;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里云 OSS 配置类
 * 作用：创建 OSS 客户端 Bean，交给 Spring 管理
 */
@Configuration
public class Oss {

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;

    /**
     * 初始化 OSS 客户端，全局单例
     */
    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder()
                .build(endpoint, accessKeyId, accessKeySecret);
    }
}