package com.abc.dddtemplate.adapter.configure.orika;

import lombok.Data;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.stereotype.Component;

/**
 * @author qiaohe
 * @date 2023-03-09
 */
@Component
public class DemoC2DCustomMapperBuilder extends OrikaMapper.ClassMapperBuilderAutoConfiguration<DemoC2DCustomMapperBuilder.C, DemoC2DCustomMapperBuilder.D> {
    public DemoC2DCustomMapperBuilder() {
        super(C.class, D.class);
    }

    @Override
    public void configure(ClassMapBuilder<C,D> builder, MapperFactory mapperFactory) {
        builder.field("name", "nick");
    }

    @Override
    public void configureReverse(ClassMapBuilder<D,C> builder, MapperFactory mapperFactory) {
        builder.field("nick", "name");
    }


    @Data
    public static class C {
        private String x;
        private String age;
        private String name;
    }

    @Data
    public static class D {
        private String x;
        private Integer age;
        private String nick;
    }
}
