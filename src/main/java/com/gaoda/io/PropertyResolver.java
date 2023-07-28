package com.gaoda.io;

import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.function.Function;

public class PropertyResolver {

    Logger logger = LoggerFactory.getLogger(getClass());

    Map<String, String> properties = new HashMap<>();

    /**
     * 对于类型转换，实际上是从String转换为指定类型，因此，用函数式接口Function<String, Object>就很合适：
     */
    // 存储Class -> Function:
    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();


    public PropertyResolver(Properties props) {
        // 存入环境变量
        this.properties.putAll(System.getenv());
        // 存入Properties
        Set<String> names = props.stringPropertyNames();
        for (String name : names) {
            this.properties.put(name, props.getProperty(name));
        }
        if (logger.isDebugEnabled()) {
            List<String> keys = new ArrayList<>(this.properties.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                logger.debug("PropertyResolver: {} = {}", key, this.properties.get(key));
            }
        }
        // register converters:
        // 把各种要转换的类型放到Map里
        converters.put(String.class, s -> s);
        converters.put(boolean.class, s -> Boolean.parseBoolean(s));
        converters.put(Boolean.class, s -> Boolean.valueOf(s));

        converters.put(byte.class, s -> Byte.parseByte(s));
        converters.put(Byte.class, s -> Byte.valueOf(s));

        converters.put(short.class, s -> Short.parseShort(s));
        converters.put(Short.class, s -> Short.valueOf(s));

        converters.put(int.class, s -> Integer.parseInt(s));
        converters.put(Integer.class, s -> Integer.valueOf(s));

        converters.put(long.class, s -> Long.parseLong(s));
        converters.put(Long.class, s -> Long.valueOf(s));

        converters.put(float.class, s -> Float.parseFloat(s));
        converters.put(Float.class, s -> Float.valueOf(s));

        converters.put(double.class, s -> Double.parseDouble(s));
        converters.put(Double.class, s -> Double.valueOf(s));

        converters.put(LocalDate.class, s -> LocalDate.parse(s));
        converters.put(LocalTime.class, s -> LocalTime.parse(s));
        converters.put(LocalDateTime.class, s -> LocalDateTime.parse(s));
        converters.put(ZonedDateTime.class, s -> ZonedDateTime.parse(s));
        converters.put(Duration.class, s -> Duration.parse(s));
        converters.put(ZoneId.class, s -> ZoneId.of(s));
        /**
         * 如果再加一个registerConverter()接口，我们就可以对外提供扩展，让用户自己编写自己定制的Converter，
         */

    }

    public boolean containsProperty(String key) {
        return this.properties.containsKey(key);
    }

    @Nullable
    public String getProperty(String key) {
        // 解析${abc.xyz:defaultValue}:
        PropertyExpr keyExpr = parsePropertyExpr(key);
        if (keyExpr != null) {
            if (keyExpr.defaultValue() != null) {
                // 带默认值查询:
                return getProperty(keyExpr.key(), keyExpr.defaultValue());
            } else {
                // 不带默认值查询:
                return getRequiredProperty(keyExpr.key());
            }
        }
        // 普通key查询:
        String value = this.properties.get(key);
        if (value != null) {
            // 每次查询到value后，我们递归调用parseValue()，这样就可以支持嵌套的key，例如：
            // ${app.title:${APP_NAME:Summer}}
            // 这样可以先查询app.title，没有找到就再查询APP_NAME，还没有找到就返回默认值Summer。
            return parseValue(value);
        }
        return value;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? parseValue(defaultValue) : value;
    }

    /**
     * 除了String类型外，@Value注入时，还允许boolean、int、Long等基本类型和包装类型。
     * 此外，Spring还支持Date、Duration等类型的注入。我们既要实现类型转换，又不能写死，否则，将来支持新的类型时就要改代码。
     * @param key
     * @param targetType
     * @return
     * @param <T>
     */
    @Nullable
    public <T> T getProperty(String key, Class<T> targetType) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        // 转换为指定类型:
        return convert(targetType, value);
    }


    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        // 转换为指定类型
        return convert(targetType, value);
    }

    public String getRequiredProperty(String key) {
        String value = getProperty(key);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    public <T> T getRequiredProperty(String key, Class<T> targetType) {
        T value = getProperty(key, targetType);
        return Objects.requireNonNull(value, "Property '" + key + "' not found.");
    }

    /**
     * 转换到指定Class类型
     * @param clazz
     * @param value
     * @return
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    <T> T convert(Class<?> clazz, String value) {
        Function<String, Object> fn = this.converters.get(clazz);
        if (fn == null) {
            throw new IllegalArgumentException("Unsupported value type: " + clazz.getName());
        }
        return (T) fn.apply(value);
    }

    String parseValue(String value) {
        PropertyExpr expr = parsePropertyExpr(value);
        if (expr == null) {
            return value;
        }
        if (expr.defaultValue() != null) {
            return getProperty(expr.key(), expr.defaultValue());
        } else {
            return getRequiredProperty(expr.key());
        }
    }

    /**
     * 按${...}解析：
     * @param key
     * @return
     */
    PropertyExpr parsePropertyExpr(String key) {
        if (key.startsWith("${") && key.endsWith("}")) {
            // 是否存在defaultValue?
            int n = key.indexOf(':');
            if (n == (-1)) {
                // no default value: ${key}
                String k = notEmpty(key.substring(2, key.length() - 1));
                return new PropertyExpr(k, null);
            } else {
                // has default value: ${key:default}
                String k = notEmpty(key.substring(2, n));
                return new PropertyExpr(k, key.substring(n + 1, key.length() - 1));
            }
        }
        return null;
    }

    String notEmpty(String key) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("Invalid key: " + key);
        }
        return key;
    }
}

/**
 * 定义一个PropertyExpr，把解析后的key和defaultValue存储起来：
 * @param key
 * @param defaultValue
 */
record PropertyExpr(String key, String defaultValue) {
}