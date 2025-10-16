package net.risesoft.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public class BeanFactory {

    /**
     * 向BeanFactory中添加实体类对象
     *
     * @param beanFactory spring的BeanFactory
     * @param beanName 实体类的名称，例如net.risesoft.service.Test1
     * @return DefaultListableBeanFactory
     */
    public static DefaultListableBeanFactory addBean(DefaultListableBeanFactory beanFactory, String beanName) {
        if (!beanFactory.containsBean(beanName)) {
            BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(beanName);
            bdb.setScope("prototype");
            beanFactory.registerBeanDefinition(beanName, bdb.getBeanDefinition());
        }
        return beanFactory;
    }

    /**
     * 从spring应用上下文中获取BeanFactory
     *
     * @param applicationContext spring应用上下文
     * @return DefaultListableBeanFactory
     */
    public static DefaultListableBeanFactory getBeanFactory(ConfigurableApplicationContext applicationContext) {
        return (DefaultListableBeanFactory)applicationContext.getBeanFactory();
    }
}
