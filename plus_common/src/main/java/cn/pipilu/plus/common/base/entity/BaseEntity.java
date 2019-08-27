package cn.pipilu.plus.common.base.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;
@Data
@ToString
//@MappedSuperclass
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 7931656564364552127L;
    private String id;
}
