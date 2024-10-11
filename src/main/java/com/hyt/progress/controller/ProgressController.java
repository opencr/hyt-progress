package com.hyt.progress.controller;


import com.hyt.progress.annotation.HytProgress;
import com.hyt.progress.util.ProgressManager;
import com.hyt.progress.entity.Result;
import com.hyt.progress.service.ProgressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Resource
    private ProgressService progressService;
    @Resource
    private ProgressManager progressManager;

    @GetMapping("/get")
    public Result get(@RequestParam String key) {
        return Result.success(progressService.get(key));
    }

    @GetMapping("/test")
    @HytProgress
    public void test() {
        progressManager.finish("111");
    }

}
