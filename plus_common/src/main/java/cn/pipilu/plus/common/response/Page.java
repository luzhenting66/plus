package cn.pipilu.plus.common.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Page<T> implements Serializable {
    private Long total;
    private List<T> dataList;
}
