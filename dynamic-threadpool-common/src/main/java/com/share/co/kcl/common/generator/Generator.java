package com.share.co.kcl.common.generator;

/**
 * 生成器接口
 *
 * @param <T> 返回类型
 */
public interface Generator<T> {
    /**
     * 下一个节点
     */
    T next();
}
