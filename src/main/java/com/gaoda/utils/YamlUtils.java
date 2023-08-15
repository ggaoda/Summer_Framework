package com.gaoda.utils;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;
import org.yaml.snakeyaml.resolver.Resolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Spring Framework并不支持YAML配置，但Spring Boot支持。因为YAML配置比.properties要方便，所以我们把对YAML的支持也集成进来。
 *
 * 首先引入依赖org.yaml:snakeyaml:2.0，然后我们写一个YamlUtils，通过loadYamlAsPlainMap()方法读取一个YAML文件，并返回Map：
 *
 * Parse yaml by snakeyaml:
 * 
 * https://github.com/snakeyaml/snakeyaml
 */
@SuppressWarnings("unused")
public class YamlUtils {

    @SuppressWarnings("unchecked")
    public static Map<String, Object> loadYaml(String path) {
        LoaderOptions loaderOptions = new LoaderOptions();
        DumperOptions dumperOptions = new DumperOptions();
        Representer representer = new Representer(dumperOptions);
        NoImplicitResolver resolver = new NoImplicitResolver();
        Yaml yaml = new Yaml(new Constructor(loaderOptions), representer, dumperOptions, loaderOptions, resolver);
        return ClassPathUtils.readInputStream(path, (input) -> {
            return (Map<String, Object>) yaml.load(input);
        });
    }

    /**
     * 读取一个YAML文件，并返回Map
     * @param path
     * @return
     */
    public static Map<String, Object> loadYamlAsPlainMap(String path) {
        Map<String, Object> data = loadYaml(path);
        Map<String, Object> plain = new LinkedHashMap<>();
        convertTo(data, "", plain);
        return plain;
    }

    static void convertTo(Map<String, Object> source, String prefix, Map<String, Object> plain) {
        for (String key : source.keySet()) {
            Object value = source.get(key);
            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> subMap = (Map<String, Object>) value;
                convertTo(subMap, prefix + key + ".", plain);
            } else if (value instanceof List) {
                plain.put(prefix + key, value);
            } else {
                plain.put(prefix + key, value.toString());
            }
        }
    }
}

/**
 * Disable ALL implicit convert and treat all values as string.
 * 默认将所有的类型转换都转为String处理
 */
class NoImplicitResolver extends Resolver {

    public NoImplicitResolver() {
        super();
        super.yamlImplicitResolvers.clear();
    }
}