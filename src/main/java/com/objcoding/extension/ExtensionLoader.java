package com.objcoding.extension;


import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang.StringUtils;

/**
 * the extension loader
 *
 * @author zhangchenghui.dev@gmail.com
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class ExtensionLoader<T> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExtensionLoader.class);

    /**
     * 扩展接口
     */
    private final Class<T> tClass;

    /**
     * 扩展实现缓存
     */
    private final Map<String/* extensionName */, T> extensionCache = new ConcurrentHashMap<>();

    /**
     * 默认扩展实现，{@link SPI#defaultExtension()}
     */
    private String defaultExtensionName;

    public ExtensionLoader(final Class<T> tClass) {
        this.tClass = tClass;
        loadDefaultExtensionName();
        loadExtension();
    }

    /**
     * 获取扩展默认实现类
     *
     * @return default extension
     */
    public T getDefaultExtension() {
        return getExtension(defaultExtensionName);
    }

    /**
     * 获取扩展实现类
     *
     * @param extensionName 扩展实现名，{@link Extension#name()}
     * @return extension
     */
    public T getExtension(String extensionName) {
        if (StringUtils.isBlank(extensionName)
            && StringUtils.isNotBlank(defaultExtensionName)) {
            return extensionCache.get(defaultExtensionName);
        }
        return extensionCache.get(extensionName);
    }

    private void loadExtension() {
        ServiceLoader<T> tServiceLoader = ServiceLoader.load(tClass);
        Iterator<T> iterator = tServiceLoader.iterator();
        while (iterator.hasNext()) {
            T extension = iterator.next();
            Extension anno = extension.getClass().getAnnotation(Extension.class);
            if (anno != null) {
                extensionCache.put(anno.name(), extension);
            } else {
                LOGGER.warn("extension:" + extension + " has no @Extension annotation.");
            }
        }
        LOGGER.warn(tClass.getSimpleName() + " spi load extension success.");
    }

    private void loadDefaultExtensionName() {
        SPI annotation = tClass.getAnnotation(SPI.class);
        if (null != annotation) {
            String v = annotation.defaultExtension();
            if (StringUtils.isNotBlank(v)) {
                defaultExtensionName = v;
                LOGGER.warn(tClass.getName() + " spi default extension is " + defaultExtensionName);
            }
        }
    }

}