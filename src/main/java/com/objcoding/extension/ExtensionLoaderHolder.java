package com.objcoding.extension;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI 加载器缓存持有类
 *
 * @author zhangchenghui.dev@gmail.com
 * @since 1.0.0
 */
public final class ExtensionLoaderHolder {

    private ExtensionLoaderHolder() {
    }

    /**
     * Loader 缓存
     */
    private static final Map<Class<?>, ExtensionLoader<?>> LOADER_MAP = new ConcurrentHashMap<>();

    /**
     * 获取扩展加载器
     * PS: 用户想要自定义实现一些扩展类，可以自行设置 directory 进行加载
     *
     * @param tClass SPI class
     * @param <T>    SPI class 泛型
     * @return ExtensionLoader
     */
    @SuppressWarnings("all")
    public static <T> ExtensionLoader<T> getExtensionLoader(final Class<T> tClass) {
        if (null == tClass) {
            throw new RuntimeException("tClass is null !");
        }
        if (!tClass.isInterface()) {
            throw new RuntimeException("tClass :" + tClass + " is not interface !");
        }
        if (!tClass.isAnnotationPresent(SPI.class)) {
            throw new RuntimeException("tClass " + tClass + "without @" + SPI.class + " Annotation !");
        }
        return (ExtensionLoader<T>) LOADER_MAP.computeIfAbsent(tClass,
            key -> new ExtensionLoader<>(tClass));
    }
}