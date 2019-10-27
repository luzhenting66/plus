package cn.pipilu.plus.common.base.entity;

import cn.pipilu.plus.common.constant.DeleteFlagE;
import lombok.Data;
import lombok.ToString;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Data
@ToString
public  class BaseEntity implements Serializable {
    private static final long  serialVersionUID = 7931656564364552127L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long             id;

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final int DEL_FLAG_NORMAL  = DeleteFlagE.NORMAL.code;
    public static final int DEL_FLAG_DELETE  = DeleteFlagE.DELETE.code;

    public BaseEntity() {
    }

    public BaseEntity(Long id) {
        this();
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }
        BaseEntity that = (BaseEntity) obj;
        return null == this.getId() ? false : this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
