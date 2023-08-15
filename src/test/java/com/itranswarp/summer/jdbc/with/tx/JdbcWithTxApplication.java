package com.itranswarp.summer.jdbc.with.tx;

import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Import;
import com.gaoda.jdbc.JdbcConfiguration;

// 客户端引入JdbcConfiguration就自动获得了数据源：
@ComponentScan
@Configuration
@Import(JdbcConfiguration.class)
public class JdbcWithTxApplication {

}