package org.lmmarise.dynamic_properties.spring.annotation.property;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import org.springframework.beans.factory.BeanFactory;

import java.util.Collection;
import java.util.Map;

public class SpringValueRegistry {
    /**
     * 每个容器中有不同的环境，因此需要以容器为 key 进行再关联。
     */
    private final Map<BeanFactory, Multimap<String, SpringValue>> registry = Maps.newConcurrentMap();

    private final Object lock = new Object();

    public void register(BeanFactory beanFactory, String key, SpringValue springValue) {
        if (!registry.containsKey(beanFactory)) {
            synchronized (lock) {
                if (!registry.containsKey(beanFactory)) {        // 第一次初始化
                    registry.put(beanFactory, LinkedListMultimap.create());
                }
            }
        }

        // 记录 @Value("${VariableUrl}") 中 SpEL 表达式解析出来的 key，与 @Value("${VariableUrl}") 注解实例信息、以及所在的 Bean 实例的信息
        registry.get(beanFactory).put(key, springValue);
    }

    public Collection<SpringValue> get(BeanFactory beanFactory, String key) {
        Multimap<String, SpringValue> beanFactorySpringValues = registry.get(beanFactory);
        if (beanFactorySpringValues == null) {
            return null;
        }
        return beanFactorySpringValues.get(key);
    }
}
