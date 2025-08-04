package net.risesoft.service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AutoFormSequenceService {

    /**
     *
     * @param patternLength 要生成的总长度
     * @param sequence 序列号
     * @return
     */
    String calculateSequence(int patternLength, int sequence);

    /**
     * 生成带有格式的序列号
     *
     * @param tenantId
     * @param labelName
     * @return
     */
    String genSequence(String tenantId, String labelName, String character);

    /**
     * 获取序列号，并更新数据库表中的值，这里暂时没有考虑并发的情况，先实现了功能 对并发的处理有如下几种考虑： 1、使用jdbctemplate，在sql语句中设置锁的级别是Serializable
     * 2、对每一个标签生成一个表，在程序中生成一个32位码并将其插入到数据表中，从数据表中查找该32位码对应的自增字段， 优点是记录当前的序列号是否被使用过，缺点是每个序列号都要一个数据表与之对应 3、使用mongdb的原子性操作
     *
     * @param tenantId
     * @param labelName
     * @return
     */
    Integer getSequence(String tenantId, String labelName, String character);

    /**
     * 更新指定的序列号，执行加一操作
     *
     * @param tenantId
     * @param labelName
     */
    void updateSequence(String tenantId, String labelName, String character);
}
