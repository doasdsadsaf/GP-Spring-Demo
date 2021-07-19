package com.gupaodu.vip.spring.demo.service.Impl;

import com.gupaodu.vip.spring.demo.service.IQueryService;
import com.gupaodu.vip.spring.formework.annotation.GPService;

/**
 * @创建人 dw
 * @创建时间 2021/7/19
 * @描述
 */
@GPService
public class QueryServiceImpl implements IQueryService {

    public String getName(String name) {
        return name;
    }
}
