package com.abc.dddtemplate.share.util;

import ma.glasnost.orika.Converter;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import ma.glasnost.orika.metadata.MappingDirection;
import ma.glasnost.orika.metadata.Property;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.property.IntrospectorPropertyResolver;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

/**
 *
 */
public class MapperUtil {

    /**
     * 映射单个实例
     *
     * @param obj
     * @param desClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D map(S obj, Class<D> desClass) {
        if (Objects.isNull(obj)) {
            return null;
        }
        D des = getMapperFacade(obj.getClass(), desClass).map(obj, desClass);
        return des;
    }

    /**
     * 映射单个实例
     *
     * @param obj
     * @param d
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D map(S obj, D d) {
        if (Objects.isNull(obj)) {
            return d;
        }
        if (Objects.isNull(d)) {
            throw new NullPointerException("参数d为空");
        }
        if (!Objects.isNull(obj)) {
            getMapperFacade(obj.getClass(), d.getClass()).map(obj, d);
        }
        return d;
    }

    /**
     * 映射列表
     *
     * @param list
     * @param desClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> List<D> mapAsList(Iterable<S> list, Class<D> desClass) {
        List<D> result = new ArrayList<>();
        if (IterableUtils.isEmpty(list)) {
            return result;
        }
        list.forEach(i -> {
            D d = map(i, desClass);
            result.add(d);
        });
        return result;
    }

    /**
     * 映射列表
     *
     * @param arr
     * @param desClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> List<D> mapAsList(S[] arr, Class<D> desClass) {
        List<D> result = new ArrayList<>();
        if (ArrayUtils.isEmpty(arr)) {
            return result;
        }
        for (S i : arr) {
            D d = map(i, desClass);
            result.add(d);
        }
        return result;
    }

    /**
     * 配置映射规则
     *
     * @param srcClass  源类型
     * @param destClass 目标类型
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> MapperConfigBuilder<S, D> config(Class<S> srcClass, Class<D> destClass) {
        return new MapperConfigBuilder<>(srcClass, destClass);
    }

    /**
     * 获取Orika映射工厂
     *
     * @return
     */
    public static MapperFactory getOrikaMapperFactory() {
        return MapperConfigBuilder.MAPPER_FACTORY;
    }

    public static MapperFacade getMapperFacade(Class<?> srcClass, Class<?> desClass) {
        if (MapperConfigBuilder.MAPPER_FACADE == null) {
            MapperConfigBuilder.MAPPER_FACADE = MapperConfigBuilder.MAPPER_FACTORY.getMapperFacade();
        }

        if (isEntityBuilder(desClass)) {
            config(srcClass, desClass).apply();
        }
        return MapperConfigBuilder.MAPPER_FACADE;
    }

    private static Map<Class, Boolean> IS_ENTITY_BUILDER_CACHE = new HashMap<>();
    /**
     * 判定是否是实体构造器
     *
     * @param type
     * @return
     */
    private static boolean isEntityBuilder(Class<?> type) {
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

    /**
     * 映射配置构建器
     *
     * @param <S>
     * @param <D>
     */
    public static class MapperConfigBuilder<S, D> {

        /**
         * 默认字段工厂
         */
        private static final DefaultMapperFactory MAPPER_FACTORY = new DefaultMapperFactory.Builder()
                .propertyResolverStrategy(new CustomerPropertyResolver())
                .build();

        /**
         * 默认映射对象
         */
        private static MapperFacade MAPPER_FACADE;

        private final Class<S> srcClass;
        private final Class<D> destClass;
        private final ClassMapBuilder<S, D> classMapBuilder;

        protected MapperConfigBuilder(Class<S> srcClass, Class<D> destClass) {
            this.srcClass = srcClass;
            this.destClass = destClass;
            classMapBuilder = MAPPER_FACTORY.classMap(srcClass, destClass);
        }

        /**
         * 映射字段
         *
         * @param srcField
         * @param destField
         * @return
         */
        public MapperConfigBuilder<S, D> field(String srcField, String destField) {
            classMapBuilder.field(srcField, destField);
            return this;
        }

        /**
         * 映射字段，自定义转换器
         *
         * @param srcField
         * @param destField
         * @param fieldConverter
         * @return
         */
        public MapperConfigBuilder<S, D> field(String srcField, String destField, Function<Object, Object> fieldConverter) {
            String converterId = String.format("%s.%s->%s.%s", srcClass.getName(), srcField, destClass.getName(), destField);
            MAPPER_FACTORY.getConverterFactory().registerConverter(converterId, new CommonFieldConverter(fieldConverter));
            classMapBuilder.fieldMap(srcField, destField)
                    .direction(MappingDirection.BIDIRECTIONAL)
                    .converter(converterId)
                    .add();
            return this;
        }

        /**
         * 排除字段，
         *
         * @param fields 这些字段不会映射
         * @return
         */
        public MapperConfigBuilder<S, D> exclude(String... fields) {
            for (String field : fields) {
                classMapBuilder.exclude(field);
            }
            return this;
        }

        private static Map<String, Boolean> BUILDER_CONFIG_CACHE = new HashMap<>();
        /**
         * 应用配置
         */
        public void apply() {
            if (isEntityBuilder(destClass)) {
                String key = String.format("%s->%S",srcClass.getName(), destClass.getName());
                if(BUILDER_CONFIG_CACHE.containsKey(key)){
                    return;
                }
                classMapBuilder.mapNulls(false);
                BUILDER_CONFIG_CACHE.put(key, true);
            }
            classMapBuilder.byDefault().register();
            if (MAPPER_FACADE != null) {
                MAPPER_FACADE = MAPPER_FACTORY.getMapperFacade();
            }
        }

        /**
         * 获取Orika映射配置构建器
         *
         * @return
         */
        public ClassMapBuilder<S, D> getClassMapBuilder() {
            return classMapBuilder;
        }
    }

    /**
     * 自定义属性解析
     */
    public static class CustomerPropertyResolver extends IntrospectorPropertyResolver {

        @Override
        protected void collectProperties(Class<?> type, Type<?> referenceType, Map<String, Property> properties) {
            super.collectProperties(type, referenceType, properties);

            if (!isEntityBuilder(type)) {
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