package net.risesoft.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 对spring的BeanFactory的操作
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public class BeanFactory {

    /**
     * 向BeanFactory中添加实体类对象
     * 
     * @param beanFactory spring的BeanFactory
     * @param beanName 实体类的名称，例如net.risesoft.service.Test1
     * @return
     */
    public static DefaultListableBeanFactory addBean(DefaultListableBeanFactory beanFactory, String beanName) {
        // beanName="net.risesoft.service.Test1";
        if (!beanFactory.containsBean(beanName)) {
            BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(beanName);
            bdb.setScope("prototype");
            beanFactory.registerBeanDefinition(beanName, bdb.getBeanDefinition());
        }
        return beanFactory;
    }

    /**
     * 从BeanFactory中删除实体类对象
     * 
     * @param beanFactory spring的BeanFactory
     * @param beanName 实体类的名称，例如net.risesoft.service.Test1
     */
    public static void delBean(DefaultListableBeanFactory beanFactory, String beanName) {

    }

    /**
     * 从spring应用上下文中获取BeanFactory
     * 
     * @param applicationContext spring应用上下文
     * @return
     */
    public static DefaultListableBeanFactory getBeanFactory(ConfigurableApplicationContext applicationContext) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory)applicationContext.getBeanFactory();
        return beanFactory;
    }

}
