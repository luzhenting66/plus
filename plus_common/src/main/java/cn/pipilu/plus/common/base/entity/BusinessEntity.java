package cn.pipilu.plus.common.base.entity;

import cn.pipilu.plus.common.request.Request;
import cn.pipilu.plus.common.util.JwtUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.jsonwebtoken.Claims;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

@Data
@ToString
public class BusinessEntity extends BaseEntity {
    private static final long serialVersionUID = -8961198914502437535L;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;
    protected String createBy;
    protected String updateBy;
    protected String remark;//备注
    protected boolean delFlag;//删除标记（0-正常、1-删除、2-审核）
    protected String code;//编码

    public BusinessEntity() {
        super();
        this.delFlag = false;
    }

    public BusinessEntity(Long id) {
        super(id);
    }

    public BusinessEntity(String code) {
        super();
        this.code = code;
    }

    public void preInsert() {
        Request request = new Request();
        String token = request.getToken();
        if (StringUtils.isNoneBlank(token)) {
            JwtUtil jwtUtil = new JwtUtil();
            Claims claims = jwtUtil.parseJWT(token);
            this.createBy = claims.getId();
            this.updateBy = createBy;
        }
        this.updateTime = new Date();
        this.createTime = this.updateTime;
    }

    public void preUpdate() {
        Request request = new Request();
        String token = request.getToken();
        if (StringUtils.isNoneBlank(token)) {
            JwtUtil jwtUtil = new JwtUtil();
            Claims claims = jwtUtil.parseJWT(token);
            this.updateBy = claims.getId();
        }
        this.updateTime = new Date();
    }

    public void preInsert(String userCode) {
        this.createBy = userCode;
        this.updateBy = createBy;
        this.updateTime = new Date();
        this.createTime = this.updateTime;
    }

    public void preUpdate(String userCode) {
        this.updateBy = userCode;
        this.updateTime = new Date();
    }
}
