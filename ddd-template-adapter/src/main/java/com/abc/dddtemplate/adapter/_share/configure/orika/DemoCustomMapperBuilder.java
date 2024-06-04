package com.abc.dddtemplate.adapter._share.configure.orika;

import lombok.Data;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.springframework.stereotype.Component;

/**
 * @author <template/>
 * @date 2023-03-09
 */
@Component
public class DemoCustomMapperBuilder extends OrikaMapper.ClassMapperBuilderAutoConfiguration<DemoCustomMapperBuilder.A, DemoCustomMapperBuilder.B> {
    public DemoCustomMapperBuilder() {
        super(A.class, B.class);
    }

    @Override
    public void configure(ClassMapBuilder<A,B> builder, MapperFactory mapperFactory) {
        builder.field("name", "nick");
    }

    @Override
    public void configureReverse(ClassMapBuilder<B, A> builder, MapperFactory mapperFactory) {
        builder.field("nick", "name");
    }


    @Data
    public static class A {
        private String x;
        private String age;
        private String name;
    }

    @Data
    public static class B {
        private String x;
        private Integer age;
        private String nick;
    }
}
