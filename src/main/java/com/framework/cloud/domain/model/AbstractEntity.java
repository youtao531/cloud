package com.framework.cloud.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Entity 抽象父类
 *
 * @author youtao531 2024/10/3 18:16
 */
@Getter
public class AbstractEntity<T extends Serializable> implements Serializable {

    /**
     * 默认主键字段ID，类型为Long型自增，转为Json时转换为String
     */
    @TableId(type = IdType.AUTO)
    private T id;

    public AbstractEntity<T> setId(T id) {
        this.id = id;
        return this;
    }

    /**
     * Entity 对象转为String
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + this.getId();
    }
}
