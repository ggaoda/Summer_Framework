package com.itranswarp.summer.jdbc.with.tx;

import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Configuration;
import com.gaoda.annotation.Import;
import com.gaoda.jdbc.JdbcConfiguration;

@ComponentScan
@Configuration
@Import(JdbcConfiguration.class)
public class JdbcWithTxApplication {

}