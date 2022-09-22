package com.share.co.kcl.common.utils;

import com.share.co.kcl.dtp.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtilsTest {

    @Test
    public void nowTest() {
        Assert.assertEquals("now method failure", LocalDateTime.now(), DateUtils.now());
        Assert.assertEquals("now method failure", LocalDateTime.now(), DateUtils.now());
        Assert.assertEquals("now method failure", LocalDateTime.now(), DateUtils.now());
    }

    @Test
    public void afterTest() {
        Assert.assertEquals("after method failure", System.currentTimeMillis() + 30000, DateUtils.valueOfMilliSecond(DateUtils.after(30, ChronoUnit.SECONDS)));
        Assert.assertEquals("after method failure", System.currentTimeMillis() + 40000, DateUtils.valueOfMilliSecond(DateUtils.after(40, ChronoUnit.SECONDS)));
        Assert.assertEquals("after method failure", System.currentTimeMillis() + 50000, DateUtils.valueOfMilliSecond(DateUtils.after(50, ChronoUnit.SECONDS)));
    }

    @Test
    public void valueOfTest() {
        Assert.assertEquals("valueOfMilliSecond method failure", System.currentTimeMillis(), DateUtils.valueOfMilliSecond(LocalDateTime.now()));
        Assert.assertEquals("valueOfSecond method failure", System.currentTimeMillis() / 1000, DateUtils.valueOfSecond(LocalDateTime.now()));
    }
}
