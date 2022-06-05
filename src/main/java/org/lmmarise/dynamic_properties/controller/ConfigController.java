package org.lmmarise.dynamic_properties.controller;

import org.lmmarise.dynamic_properties.spring.annotation.SpringValueProcessor;
import org.lmmarise.dynamic_properties.spring.annotation.property.SpringValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

@RestController
public class ConfigController {

    @Value("${VariableName:匿名}")
    private String name;

    @Value("${VariableUrl}")
    private String VariableUrl;

    private final SpringValueProcessor springValueProcessor;
    private final ConfigurableBeanFactory beanFactory;

    public ConfigController(SpringValueProcessor springValueProcessor, ConfigurableBeanFactory beanFactory) {
        this.springValueProcessor = springValueProcessor;
        this.beanFactory = beanFactory;
    }

    @GetMapping("/get")
    public String get() {
        return name + "：1" + VariableUrl;
    }

    @GetMapping("/updateName")
    public String updateName(String newName) {
        // 拿到所有通过 @Value 注入了 VariableName 属性的 bean 及字段信息
        Collection<SpringValue> targetValues = springValueProcessor.springValueRegistry.get(beanFactory, "VariableName");
        for (SpringValue val : targetValues) {
            try {
                val.update(newName);           // 通过反射进行修改
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this.name;           // 返回修改后的效果
    }
}
