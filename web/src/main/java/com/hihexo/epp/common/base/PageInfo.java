package com.hihexo.epp.common.base;

import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

public class PageInfo<T> {

    private int pageNum = 1;//第几页  从1 开始
    private int pageSize = 10; //每页显示记录数 默认 10
    private  int total; //总数统计
    private List<T> rows; //记录列表集合

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    private PageInfo(){
    }

    /**
     * page --> PageInfo
     * @param page
     * @param <T>
     * @return
     */
    public static <T> PageInfo<T> convertPageInfo(Page<T> page){
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(page.getCurrent());
        pageInfo.setPageSize(page.getSize());
        pageInfo.setTotal(page.getTotal());
        pageInfo.setRows(page.getRecords());
        return pageInfo;
    }
}
