package top.xym.web.rag.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ali_oss_file")
public class AliOssFile {

    @TableId(type = IdType.AUTO)
    private Long id;

    // 原始文件名
    private String fileName;
    // OSS访问地址
    private String url;
    // 向量文档ID集合
    private String vectorId;
    // 0：启用；1：禁用
    private Integer status;
    private LocalDate createTime;
    private LocalDate updateTime;
}