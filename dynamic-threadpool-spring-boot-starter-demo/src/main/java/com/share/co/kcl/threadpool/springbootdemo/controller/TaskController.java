package com.share.co.kcl.threadpool.springbootdemo.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "任务路由")
@RequestMapping(value = "/task")
public class TaskController {

    public void submit(){

    }
}
