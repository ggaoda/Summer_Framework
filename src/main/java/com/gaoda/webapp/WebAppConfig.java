package com.gaoda.webapp;

import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;

/**
 * 需要的@Configuration配置类从哪获取？这是通过web.xml文件配置的：
 */
@ComponentScan
@Configuration
public class WebAppConfig {

}