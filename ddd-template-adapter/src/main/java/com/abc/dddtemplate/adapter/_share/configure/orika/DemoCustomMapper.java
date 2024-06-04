package com.abc.dddtemplate.adapter._share.configure.orika;

import lombok.Data;
import ma.glasnost.orika.MappingContext;
import org.springframework.stereotype.Component;

/**
 * @author <template/>
 * @date 2023-02-25
 */
@Component
public class DemoCustomMapper extends OrikaMapper.CustomMapperAutoConfiguration<DemoCustomMapper.A, DemoCustomMapper.B> {

    @Override
    public void mapAtoB(A a, B b, MappingContext context) {
        b.x = a.x;
        b.age = Integer.parseInt(a.age);
        b.nick = a.name;
    }

    @Override
    public void mapBtoA(B b, A a, MappingContext context) {
        a.x = b.x;
        a.age = b.age.toString();
        a.name = b.nick;
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
