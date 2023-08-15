package com.gaoda.jdbc.tx;

import com.gaoda.annotation.Transactional;
import com.gaoda.aop.AnnotationProxyBeanPostProcessor;

/**
 * 需要提供一个TransactionalBeanPostProcessor，使得AOP机制生效，才能拦截@Transactional标注的Bean的public方法：
 */
public class TransactionalBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Transactional> {

}