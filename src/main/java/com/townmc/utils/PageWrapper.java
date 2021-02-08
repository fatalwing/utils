package com.townmc.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 分页数据包装类
 * @author meng
 */
public class PageWrapper<T> {
    /**
     * 数据内容
     */
    private List<T> content;
    /**
     * 当前查询页（从0开始）
     */
    private Integer number;
    /**
     * 当前查询页项目数量
     */
    private Integer numberOfElements;
    /**
     * 总项目数量
     */
    private Long totalElements;
    /**
     * 总页数
     */
    private Integer totalPages;
    /**
     * 每页大小
     */
    private Integer size;
    /**
     * 是否为空
     */
    private boolean empty;
    /**
     * 是否为首页
     */
    private boolean first;
    /**
     * 是否为尾页
     */
    private boolean last;

    /**
     * 构造分页返回数据对象
     * @param number 当前查询页码
     * @param size 分页数据条数
     * @param content 包裹数据
     * @param totalElements 总的数据条数
     */
    public PageWrapper(Integer number, Integer size, List<T> content, Long totalElements) {
        this.number = number;
        this.size = size;
        this.content = content;
        this.totalElements = totalElements;

        this.fill();
    }

    private void fill() {
        this.totalPages = new BigDecimal(this.totalElements).divide(new BigDecimal(this.size), RoundingMode.CEILING).intValue();
        this.empty = null == this.content || this.content.size() == 0;
        this.first = this.number == 0;
        this.last = this.number == this.totalPages - 1;
        this.numberOfElements = this.totalPages == 1 || this.totalPages == (this.number + 1) ? this.totalElements.intValue() % size : size;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
