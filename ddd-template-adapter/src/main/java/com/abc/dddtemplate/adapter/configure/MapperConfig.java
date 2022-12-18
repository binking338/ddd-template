package com.abc.dddtemplate.adapter.configure;

import com.abc.dddtemplate.share.util.MapperUtil;
import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author <template/>
 * @date
 */
@Configuration
public class MapperConfig {

    @PostConstruct
    public static void configMapperUtil() {
        // 在此方法中定义对象映射规则
        MapperUtil.config(A.class, B.class)
                .exclude("x")
                .field("age", "age", n -> Integer.getInteger(n.toString()))
                .field("name", "nick")
                .apply();
        A a = new A();
        a.setX("abc");
        a.setAge("123");
        a.setName("a");
        B b = MapperUtil.map(a, B.class);
    }

    @Data
    public static class A {
        private String x;
        private String age;
        private String name;
    }

    @Data
    public static class B {
        private Integer x;
        private Integer age;
        private String nick;
    }
}
