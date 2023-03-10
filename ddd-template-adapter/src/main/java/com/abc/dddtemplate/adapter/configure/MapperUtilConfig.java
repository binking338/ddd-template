package com.abc.dddtemplate.adapter.configure;

import com.abc.dddtemplate.adapter.configure.orika.OrikaMapper;
import com.abc.dddtemplate.share.util.MapperUtil;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author <template/>
 * @date 2023-02-25
 */
@Configuration
public class MapperUtilConfig {

    @Bean
    public OrikaMapper orikaMapper(@Autowired(required = false) List<OrikaMapper.MapperFactoryAutoConfiguration> mapperFactoryAutoConfigurations) {
        OrikaMapper mapper = new OrikaMapper(mapperFactoryAutoConfigurations);
        configMapperUtil(mapper);
        return mapper;
    }

    private void configMapperUtil(MapperFacade mapper) {
        MapperUtil.configMap2Class((src, clazz) -> mapper.map(src, clazz));
        MapperUtil.configMap2Instance((src, desc) -> mapper.map(src, desc));
    }
}
