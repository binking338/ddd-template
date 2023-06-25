package com.abc.dddtemplate.share.dto;

import com.abc.dddtemplate.share.util.MapperUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author <template/>
 * @date
 */
@Slf4j
@Schema(description = "分页参数")
public class PageParam {

    /**
     * 默认页码为第一页
     */
    private static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页数据为20条
     */
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大返回数据条数
     */
    private static final int MAX_PAGE_SIZE = 500;

    /**
     * 页码
     */
    @Schema(description="页码")
    @Getter
    private Integer pageNum;

    /**
     * 页大小
     */
    @Schema(description="页大小")
    @Getter
    private Integer pageSize;

    /**
     * 排序
     */
    @Schema(description="排序")
    @Getter
    @Setter
    private List<OrderDef> sort;

    public void setPageNum(Integer pageNum) {
        this.pageNum = (pageNum == null || pageNum < 1) ? DEFAULT_PAGE_NUM : pageNum;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        // 每页大小不能超过最大值
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        this.pageSize = pageSize;
    }

    /**
     * 转换参数类型
     * @param destClass
     * @param <D>
     * @return
     */
    public <D extends PageParam> D transform(Class<D> destClass) {
        D dest = MapperUtil.map(this, destClass);
        return dest;
    }

    @Data
    public static class OrderDef {
        String field;
        Boolean desc;
    }


    public Pageable toSpringData(){
        PageRequest pageRequest = null;
        if(sort == null || sort.size() == 0) {
            pageRequest = PageRequest.of(pageNum - 1, pageSize);
        } else {
            Sort orders = Sort.by(this.sort.stream().map(s -> new Sort.Order(s.desc ? Sort.Direction.DESC : Sort.Direction.ASC, s.field)).collect(Collectors.toList()));
            pageRequest = PageRequest.of(pageNum - 1, pageSize, orders);
        }
        return pageRequest;
    }

    public Pageable toSpringData(Sort sort) {
        if (sort != null) {
            return PageRequest.of(pageNum - 1, pageSize, sort);
        } else {
            return toSpringData();
        }
    }
}
