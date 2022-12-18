package com.abc.dddtemplate.share.dto;

import com.abc.dddtemplate.share.util.MapperUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 请使用 PageData.create 静态方法创建实例
 *
 * @author <template/>
 * @date
 */
@Data
@Schema( title = "分页数据")
public class PageData<T> {

    protected PageData() {
    }

    /**
     * 页码
     */
    @Schema(description="页码")
    private Integer pageNum;

    /**
     * 页大小
     */
    @Schema(description="页大小")
    private Integer pageSize;

    /**
     * 总记录数
     */
    @Schema(description="总记录数")
    private Long totalCount;

    /**
     * 记录列表
     */
    @Schema(description="记录列表")
    private List<T> list;

    /**
     * 生成空分页返回
     *
     * @param pageSize
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> PageData<T> empty(Integer pageSize, Class<T> clazz) {
        return create(pageSize, 1, 0L, new ArrayList<T>());
    }

    /**
     * 新建分页结果
     *
     * @param pageParam
     * @param list
     * @param <T>
     * @return
     */
    public static <T> PageData<T> create(PageParam pageParam, Long totalCount, List<T> list) {
        PageData<T> pageData = new PageData<>();
        pageData.pageSize = pageParam.getPageSize();
        pageData.pageNum = pageParam.getPageNum();
        pageData.totalCount = totalCount;
        pageData.list = list;
        return pageData;

    }

    /**
     * 新建分页结果
     *
     * @param pageSize
     * @param pageNum
     * @param list
     * @param <T>
     * @return
     */
    public static <T> PageData<T> create(Integer pageSize, Integer pageNum, Long totalCount, List<T> list) {
        PageData<T> pageData = new PageData<>();
        pageData.pageSize = pageSize;
        pageData.pageNum = pageNum;
        pageData.totalCount = totalCount;
        pageData.list = list;
        return pageData;
    }

    /**
     * 转换分页结果类型
     *
     * @param destClass
     * @param <D>
     * @return
     */
    public <D> PageData<D> transform(Class<D> destClass) {
        PageData<D> pageData = create(pageSize, pageNum, totalCount, MapperUtil.mapAsList(getList(), destClass));
        return pageData;
    }

    /**
     * 从JPA转换
     *
     * @return
     */
    public static <T> PageData<T> fromSpringData(Page<T> page) {
        return create(page.getPageable().getPageSize(), page.getPageable().getPageNumber() + 1, page.getTotalElements(), page.getContent());
    }

    /**
     * 从JPA转换
     *
     * @return
     */
    public static <S, D> PageData<D> fromSpringData(Page<S> page, Class<D> desClass) {
        return fromSpringData(page).transform(desClass);
    }
}
