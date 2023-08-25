package com.gaoda.hello;

import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Import;
import com.gaoda.jdbc.JdbcConfiguration;
import com.gaoda.web.WebMvcConfiguration;

@ComponentScan
@Configuration
@Import({ JdbcConfiguration.class, WebMvcConfiguration.class })
public class HelloConfiguration {

}