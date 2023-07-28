package com.itranswarp.summer.utils;

import com.gaoda.utils.YamlUtils;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 *
 *
 * 读取YAML的代码比较简单，注意要点如下：
 *
 * SnakeYaml默认读出的结构是树形结构，需要“拍平”成abc.xyz格式的key；
 * SnakeYaml默认会自动转换int、boolean等value，需要禁用自动转换，把所有value均按String类型返回。
 */
public class YamlUtilsTest {

    @Test
    public void testLoadYaml() {
        Map<String, Object> configs = YamlUtils.loadYamlAsPlainMap("/application.yml");
        for (String key : configs.keySet()) {
            Object value = configs.get(key);
            System.out.println(key + ": " + value + " (" + value.getClass() + ")");
        }
        assertEquals("Summer Framework", configs.get("app.title"));
        assertEquals("1.0.0", configs.get("app.version"));
        assertNull(configs.get("app.author"));

        assertEquals("${AUTO_COMMIT:false}", configs.get("summer.datasource.auto-commit"));
        assertEquals("level-4", configs.get("other.deep.deep.level"));

        assertEquals("0x1a2b3c", configs.get("other.hex-data"));
        assertEquals("0x1a2b3c", configs.get("other.hex-string"));
    }
}