package top.xym.web.rag.utils;


import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * 阿里云OSS文件上传工具类
 */
@Data
@Component
public class AliOssUtil {

    // 从application.yml配置文件中读取
    @Value("${aliyun.oss.endpoint}")
    private String endpoint;
    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.oss.accessKeySecret}")
    private String accessKeySecret;
    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    /**
     * 文件上传
     * @param bytes 文件字节数组
     * @param objectName 文件名（含路径）
     * @return 文件访问URL
     */
    public String upload(byte[] bytes, String objectName) {
        // 创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        try {
            // 创建PutObject请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, new ByteArrayInputStream(bytes));
            ossClient.putObject(putObjectRequest);

            // 拼接文件访问URL
            String url = "https://" + bucketName + "." + endpoint + "/" + objectName;
            return url;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
            throw new RuntimeException("OSS文件上传失败: " + oe.getMessage());
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 生成唯一文件名（避免重名）
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    public String generateUniqueFileName(String originalFileName) {
        String suffix = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID() + suffix;
    }
}