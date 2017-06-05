package com.keluokeda;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface AdapterBean {
    /**
     * 获取 和 bean 关联的 item 的layout id
     * @return 布局id
     */
    int layoutId();
}
