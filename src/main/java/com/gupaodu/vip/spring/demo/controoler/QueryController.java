package com.gupaodu.vip.spring.demo.controoler;

import com.gupaodu.vip.spring.demo.service.IQueryService;
import com.gupaodu.vip.spring.formework.annotation.GPAutowired;
import com.gupaodu.vip.spring.formework.annotation.GPController;
import com.gupaodu.vip.spring.formework.annotation.GPRequestMapping;
import com.gupaodu.vip.spring.formework.annotation.GPRequestParam;
import com.gupaodu.vip.spring.formework.webmvc.GPModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @创建人 dw
 * @创建时间 2021/7/19
 * @描述
 */
@GPController
@GPRequestMapping("/web")
public class QueryController {

    @GPAutowired
    private IQueryService queryService;

    @GPRequestMapping("/query.json")
    public GPModelAndView query(HttpServletRequest res, HttpServletResponse rep, @GPRequestParam("name") String name) throws IOException {
        String name1 = queryService.getName(name);
        rep.getWriter().write(name1);
        return null;
    }
}
