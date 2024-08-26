package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/*
* 通用接口，主要是用于文件的上传
* */
@RestController
//请求路径
@RequestMapping("/admin/common")
@Api(tags="通用接口")//类用Api，方法用ApiOperation，方便辨认
@Slf4j
public class CommonController {
	//依赖注入，由于之前已经定义赋值好了对应的阿里云对象
	@Autowired
	private AliOssUtil aliOssUtil;


	/*
	* 上传文件操作
	* */
	@PostMapping("/upload")
	@ApiOperation("文件上传")
	public Result<String> upload(MultipartFile file){
		log.info("文件上传:{}",file);

		//调用这个阿里云工具类的upload方法
		try {
			//得到原始文件名，防止文件名重复被覆盖
			String originalFilename = file.getOriginalFilename();
			//截取原始文件名的后缀 比如说1.png的后缀就是.png
			String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			//得到新的文件名，这里进行了字符串的拼接
			String objectName = UUID.randomUUID().toString()+extension;

			//构成文件的请求路径
			//这里调用了upload方法，请求了阿里云服务
			//filePath为最终返回的网址
			String filePath = aliOssUtil.upload(file.getBytes(), objectName);
			return Result.success(filePath);
		} catch (IOException e) {
			log.error("文件上传失败:{}",e);
		}

		//上面是文件上传成功时的代码
		//下面是文件上传失败后的代码
		return Result.error("文件上传失败");
	}
}