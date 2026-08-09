package com.community.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

public class PageUtil {
    public static <T> Page<T> buildPage(long current, long size) {
        return new Page<>(current, size);
    }
}
