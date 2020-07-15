package org.javaboy.vhr.model.vo;

import java.util.List;

/**
 * 对返回的页面信息进行封装
 */
public class RespPageBean {
    // 查询页面右下角的查询到的总记录数
    private Long total;
    private List<?> data;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }
}
