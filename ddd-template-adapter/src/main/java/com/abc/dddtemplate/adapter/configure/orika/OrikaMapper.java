package com.abc.dddtemplate.adapter.configure.orika;

import lombok.RequiredArgsConstructor;
import ma.glasnost.orika.*;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.MappingDirection;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.IntrospectorPropertyResolver;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author <template/>
 * @date
 */
public class OrikaMapper extends ConfigurableMapper {
    private final List<MapperFactoryAutoConfiguration> mapperFactoryAutoConfigurations;

    public OrikaMapper(List<MapperFactoryAutoConfiguration> mapperFactoryAutoConfigurations){
        this.mapperFactoryAutoConfigurations = mapperFactoryAutoConfigurations;
        init();
    }

    /**
     * Implement this method to provide your own configurations to the Orika
     * MapperFactory used by this orika.
     *
     * @param factory the MapperFactory instance which may be used to register
     *                various configurations, mappings, etc.
     */
    @Override
    protected void configure(MapperFactory factory) {
        if (!CollectionUtils.isEmpty(mapperFactoryAutoConfigurations)) {
            mapperFactoryAutoConfigurations.forEach(config -> config.apply(factory));
        }
    }

    /**
     * Override this method only if you need to customize any of the parameters
     * passed to the factory builder, in the case that you've provided your own
     * custom implementation of one of the core components of Orika.
     *
     * @param factoryBuilder the builder which will be used to obtain a MapperFactory
     *                       instance
     */
    @Override
    protected void configureFactoryBuilder(DefaultMapperFactory.Builder factoryBuilder) {
        factoryBuilder
                .useBuiltinConverters(true)
                .useAutoMapping(true)
                .propertyResolverStrategy(new LombokBuilderPropertyResolver());
    }


    /**
     * LombokBuilder属性解析
     */
    public static class LombokBuilderPropertyResolver extends IntrospectorPropertyResolver {

        @Override
        protected void collectProperties(Class<?> type, Type<?> referenceType, Map<String, Property> properties) {
            super.collectProperties(type, referenceType, properties);

            if (!isLombokBuilder(type)) {
                return;
            }

            for (Method declaredMethod : type.getDeclaredMethods()) {
                String name = declaredMethod.getName();
                if (properties.containsKey(name) || name.equalsIgnoreCase("build")) {
                    continue;
                }
                if (declaredMethod.getParameterCount() != 1) {
                    continue;
                }
                properties.putIfAbsent(name, Property.Builder
                        .propertyFor(referenceType, name)
                        .setter(name + "(%s)")
                        .type(declaredMethod.getParameterTypes()[0])
                        .build());
            }
        }

        private static Map<Class, Boolean> IS_ENTITY_BUILDER_CACHE = new HashMap<>();

        /**
         * 判定是否是实体构造器
         *
         * @param type
         * @return
         */
        protected static boolean isLombokBuilder(Class<?> type) {
            if (IS_ENTITY_BUILDER_CACHE.containsKey(type)) {
                return IS_ENTITY_BUILDER_CACHE.get(type).booleanValue();
            }
            if (!type.getSimpleName().endsWith("Builder")) {
                IS_ENTITY_BUILDER_CACHE.put(type, false);
                return false;
            }

            try {
                Method method = type.getMethod("build");
                if (method == null) {
                    IS_ENTITY_BUILDER_CACHE.put(type, false);
                    return false;
                }
                Class<?> returnType = method.getReturnType();
                boolean isEntity = false;
                for (Annotation annotation : returnType.getAnnotations()) {
                    if (annotation.annotationType().getSimpleName().equalsIgnoreCase("Entity")) {
                        isEntity = true;
                        break;
                    }
                }
                if (!isEntity) {
                    IS_ENTITY_BUILDER_CACHE.put(type, false);
                    return false;
                }
                if (!(returnType.getSimpleName() + "Builder").equalsIgnoreCase(type.getSimpleName())) {
                    IS_ENTITY_BUILDER_CACHE.put(type, false);
                    return false;
                }
            } catch (NoSuchMethodException e) {
                IS_ENTITY_BUILDER_CACHE.put(type, false);
                return false;
            }
            IS_ENTITY_BUILDER_CACHE.put(type, true);
            return true;
        }
    }

    /**
     * 映射配置
     */
    public interface MapperFactoryAutoConfiguration {
        void apply(MapperFactory mapperFactory);
    }

    /**
     * 映射配置
     *
     * @param <S>
     * @param <D>
     */
    public static abstract class CustomMapperAutoConfiguration<S, D> extends CustomMapper<S, D> implements OrikaMapper.MapperFactoryAutoConfiguration {

        @Override
        public void apply(MapperFactory mapperFactory) {
            mapperFactory.registerMapper(this);
        }

        public abstract void mapAtoB(DemoA2BCustomMapper.A a, DemoA2BCustomMapper.B b, MappingContext context);

        public abstract void mapBtoA(DemoA2BCustomMapper.B b, DemoA2BCustomMapper.A a, MappingContext context);
    }

    /**
     * 映射配置
     *
     * @param <S>
     * @param <D>
     */
    public static abstract class ClassMapperBuilderAutoConfiguration<S, D> implements MapperFactoryAutoConfiguration {
        protected final Class<S> srcClass;
        protected final Class<D> destClass;
        private MapperFactory mapperFactory;

        protected ClassMapperBuilderAutoConfiguration(Class<S> srcClass, Class<D> destClass) {
            this.srcClass = srcClass;
            this.destClass = destClass;
        }

        /**
         * 应用配置
         *
         * @param mapperFactory
         */
        @Override
        public void apply(MapperFactory mapperFactory) {
            this.mapperFactory = mapperFactory;
            ClassMapBuilder<S, D> classMapBuilder = mapperFactory.classMap(srcClass, destClass);
            configure(classMapBuilder, mapperFactory);
            classMapBuilder.register();
            ClassMapBuilder<D, S> reverseClassMapBuilder = mapperFactory.classMap(destClass, srcClass);
            configureReverse(reverseClassMapBuilder, mapperFactory);
            reverseClassMapBuilder.register();
        }

        /**
         * 配置映射类型转换 S->D
         *
         * @param builder
         * @param mapperFactory
         */
        public abstract void configure(ClassMapBuilder<S, D> builder, MapperFactory mapperFactory);

        /**
         * 配置反向映射类型转换 D->S
         *
         * @param builder
         * @param mapperFactory
         */
        public abstract void configureReverse(ClassMapBuilder<D, S> builder, MapperFactory mapperFactory);

        /**
         * 映射字段，自定义转换器
         *
         * @param srcField
         * @param destField
         * @param fieldConverter
         * @return
         */
        protected void convertField(ClassMapBuilder classMapBuilder, String srcField, String destField, Function<Object, Object> fieldConverter) {
            String converterId = String.format("%s.%s->%s.%s", srcClass.getName(), srcField, destClass.getName(), destField);
            this.mapperFactory.getConverterFactory().registerConverter(converterId, new CommonFieldConverter(fieldConverter));
            classMapBuilder.fieldMap(srcField, destField)
                    .direction(MappingDirection.A_TO_B)
                    .converter(converterId)
                    .add();
        }

        /**
         * 自定义字段转换器，用于特定场景下的格式转换
         *
         * @param <S>
         * @param <D>
         */
        private static class CommonFieldConverter<S, D> implements Converter<S, D> {
            private final Function<S, D> converter;

            public CommonFieldConverter(Function<S, D> converter) {
                this.converter = converter;
            }

            @Override
            public boolean canConvert(Type<?> type, Type<?> type1) {
                return true;
            }

            @Override
            public D convert(S s, Type<? extends D> type, MappingContext mappingContext) {
                return converter.apply(s);
            }

            @Override
            public void setMapperFacade(MapperFacade mapperFacade) {

            }

            @Override
            public Type<S> getAType() {
                return null;
            }

            @Override
            public Type<D> getBType() {
                return null;
            }
        }
    }

}
