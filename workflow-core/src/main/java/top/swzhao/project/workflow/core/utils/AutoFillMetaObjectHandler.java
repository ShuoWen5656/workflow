package top.swzhao.project.workflow.core.utils;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * @author swzhao
 * @date 2023/11/19 3:28 下午
 * @Discreption <>
 */
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    /**
     * 新增时需要检测并自动填充的字段
     */
    private static final String[] INSERT_FIELDS = new String[]{"id", "creator", "modifier", "modificator","createTime", "updateTime"};

    /**
     * 更新时需要检查的字段
     */
    private static final String[] UPDATE_FIELDS = new String[]{"modifier", "modificator", "updateTime"};


    /**
     * 创建时自动填充id和时间
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        for (String curField : INSERT_FIELDS) {
            // 自动填充id
            if (metaObject.hasGetter(curField) && metaObject.hasSetter(curField) && Objects.isNull(this.getFieldValByName(curField, metaObject))) {
                switch (curField) {
                    case "id" :
                        this.setFieldValByName(curField, UUID.randomUUID().toString(), metaObject);
                    case "modifier" :
                    case "modificator" :
                    case "creator" :
                        this.setFieldValByName(curField, "OMP_FLOW_USER", metaObject);
                    case "createTime" :
                    case "updateTime" :
                        dealTime(curField, metaObject);
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        for (String curField : UPDATE_FIELDS) {
            // 自动填充id
            if (metaObject.hasGetter(curField) && metaObject.hasSetter(curField) && Objects.isNull(this.getFieldValByName(curField, metaObject))) {
                switch (curField) {
                    case "modifier" :
                    case "modificator" :
                        this.setFieldValByName(curField, "OMP_FLOW_USER", metaObject);
                    case "updateTime" :
                        dealTime(curField, metaObject);
                    default:
                        break;
                }
            }
        }
    }


    /**
     * 兼容主流的时间类型
     * @param curField
     * @param metaObject
     */
    private void dealTime(String curField, MetaObject metaObject) {
        Class<?> value = null;
        if (Objects.isNull(value = metaObject.getGetterType(curField))) {
            return;
        }
        if (Timestamp.class.isAssignableFrom(value)) {
            this.setFieldValByName(curField, Timestamp.valueOf(LocalDateTime.now()), metaObject);
        }else if (Date.class.isAssignableFrom(value)) {
            this.setFieldValByName(curField, new Date(System.currentTimeMillis()), metaObject);
        }else if (java.util.Date.class.isAssignableFrom(value)) {
            this.setFieldValByName(curField, new java.util.Date(System.currentTimeMillis()), metaObject);
        }else {
            this.setFieldValByName(curField, LocalDateTime.now(), metaObject);
        }
    }
}
