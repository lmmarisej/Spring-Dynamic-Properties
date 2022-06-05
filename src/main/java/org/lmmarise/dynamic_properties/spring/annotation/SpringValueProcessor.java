package org.lmmarise.dynamic_properties.spring.annotation;

import org.lmmarise.dynamic_properties.spring.annotation.property.PlaceholderHelper;
import org.lmmarise.dynamic_properties.spring.annotation.property.SpringValue;
import org.lmmarise.dynamic_properties.spring.annotation.property.SpringValueRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 记录容器中所有使用了 @Value 注解的 bean，以便对 @Value 进行热更新。
 */
@Component
public class SpringValueProcessor implements BeanPostProcessor, BeanFactoryAware {

    private final PlaceholderHelper placeholderHelper = new PlaceholderHelper();
    public SpringValueRegistry springValueRegistry = new SpringValueRegistry();

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        for (Field field : findAllField(clazz)) {
            processField(bean, beanName, field);
        }
        return bean;
    }

    /**
     * 将使用了 {@link Value} 注解的字段记录起来，以便后续动态更新。
     */
    private void processField(Object bean, String beanName, Field field) {
        // register @Value on field
        Value value = field.getAnnotation(Value.class);
        if (value == null) {
            return;
        }
        Set<String> keys = placeholderHelper.extractPlaceholderKeys(value.value());     // 解析 SpEL 表达式 key

        if (keys.isEmpty()) {
            return;
        }

        for (String key : keys) {
            SpringValue springValue = new SpringValue(key, value.value(), bean, beanName, field, false);
            springValueRegistry.register(beanFactory, key, springValue);
        }
    }

    /**
     * @return 返回指定类型上所有的字段
     */
    private List<Field> findAllField(Class<?> clazz) {
        final List<Field> res = new LinkedList<>();
        ReflectionUtils.doWithFields(clazz, res::add);
        return res;
    }
}
