package com.framework.cloud.domain.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.dromara.hutool.core.bean.BeanUtil;
import org.dromara.hutool.core.convert.ConvertUtil;
import org.dromara.hutool.json.JSONConfig;
import org.dromara.hutool.json.JSONObject;
import org.dromara.hutool.json.JSONUtil;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity 基础父类
 *
 * @author youtao531 2024/10/3 18:16
 */
@Getter
@Setter
@Accessors(chain = true)
public class BaseEntity extends AbstractEntity<Long> {

    @Override
    public BaseEntity setId(Long id) {
        super.setId(id);
        return this;
    }

    @Override
    public Long getId() {
        return super.getId();
    }

    /**
     * 默认创建时间字段，添加和更新记录时，值由数据库赋值维护(不加入SQL)
     */
    @TableField(value = "created_date", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private LocalDateTime createdDate;
    /**
     * 默认最后修改时间字段
     */
    @TableField(value = "last_modified_date", insertStrategy = FieldStrategy.NEVER, fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastModifiedDate;

    /**
     * Entity对象转为Map
     */
    public Map<String, Object> toMap() {
        return ConvertUtil.toMap(String.class, Object.class, this);
    }

    /**
     * Entity对象转为Map
     */
    public JSONObject toJson() {
        return JSONUtil.parseObj(this, JSONConfig.of().setIgnoreNullValue(false));
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

    /**
     * 获取主键值
     */
    @JsonIgnore
    public Object getPrimaryKeyVal() {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.getClass());
        if (null == tableInfo) {
            return null;
        }
        return BeanUtil.getProperty(this, tableInfo.getKeyProperty());
    }
}