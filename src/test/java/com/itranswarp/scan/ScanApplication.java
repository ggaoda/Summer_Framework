package com.itranswarp.scan;

import com.gaoda.annotation.ComponentScan;
import com.gaoda.annotation.Import;
import com.itranswarp.imported.LocalDateConfiguration;
import com.itranswarp.imported.ZonedDateConfiguration;

@ComponentScan
@Import({ LocalDateConfiguration.class, ZonedDateConfiguration.class })
public class ScanApplication {

}