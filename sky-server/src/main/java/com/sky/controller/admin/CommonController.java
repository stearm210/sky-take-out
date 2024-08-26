package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
* 通用接口，主要是用于文件的上传
* */
@RestController
//请求路径
@RequestMapping("/admin/common")
@Api(tags="通用接口")
@Slf4j
public class CommonController {
	/*
	* 上传文件操作
	* */
	@PostMapping("/upload")
	@ApiOperation("文件上传")
	public Result<String> upload(MultipartFile file){
		log.info("文件上传:{}",file);

		//

		return null;
	}
}