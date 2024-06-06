package com.abc.dddtemplate.adapter.infra.mapper;

import com.abc.dddtemplate.application.queries.SearchOrderQry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper
 *
 * @author qiaohe
 * @date 2024/6/7
 */
@Mapper
public interface OrderMapper {

    List<SearchOrderQry.SearchOrderQryDto> search(@Param("owner") String owner, @Param("key") String  key);
}
