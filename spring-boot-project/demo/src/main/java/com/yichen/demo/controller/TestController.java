package com.yichen.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qiuxinchao
 * @version 1.0
 * @date 2022/7/31 13:49
 * @describe
 */
@RestController
@Slf4j
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/get")
	public String get(){
		log.info("get");
		return "ok";
	}

}
