package cn.pipilu.plus.common.base.entity;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class BusinessEntity extends BaseEntity {

    private static final long serialVersionUID = -8961198914502437535L;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
}
