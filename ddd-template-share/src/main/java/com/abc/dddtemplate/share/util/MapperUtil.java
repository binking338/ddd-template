package com.abc.dddtemplate.share.util;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * POJO类型映射转换
 */
public class MapperUtil {

    private static BiFunction<Object, Class, Object> map2Class;
    private static BiConsumer<Object, Object> map2Instance;

    public static void configMap2Class(BiFunction<Object, Class, Object> config){
        map2Class = config;
    }
    public static void configMap2Instance(BiConsumer<Object, Object> config){
        map2Instance = config;
    }

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
        D des = (D) map2Class.apply(obj, desClass);
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
            map2Instance.accept(obj, d);
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
     * 映射列表
     *
     * @param list
     * @param desClass
     * @param <S>
     * @param <D>
     * @return
     */
    public static <S, D> D[] mapAsArray(Iterable<S> list, Class<D> desClass) {
        if (IterableUtils.isEmpty(list)) {
            return (D[]) new Object[0];
        }
        List<D> result = new ArrayList<>();
        list.forEach(i -> {
            D d = map(i, desClass);
            result.add(d);
        });
        return (D[]) result.toArray();
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
    public static <S, D> D[] mapAsArray(S[] arr, Class<D> desClass) {
        if (ArrayUtils.isEmpty(arr)) {
            return (D[]) new Object[0];
        }
        List<D> result = new ArrayList<>();
        for (S i : arr) {
            D d = map(i, desClass);
            result.add(d);
        }
        return (D[]) result.toArray();
    }
}