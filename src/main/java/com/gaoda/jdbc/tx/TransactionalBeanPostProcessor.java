package com.gaoda.jdbc.tx;

import com.gaoda.annotation.Transactional;
import com.gaoda.aop.AnnotationProxyBeanPostProcessor;

public class TransactionalBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {

}