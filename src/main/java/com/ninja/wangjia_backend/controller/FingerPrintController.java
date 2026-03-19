package com.ninja.wangjia_backend.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.entity.Fingerprint;
import com.ninja.wangjia_backend.service.FingerprintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/fingerprint")
public class FingerPrintController {
    @Resource
    private FingerprintService fingerprintService;
    @GetMapping("/set")
    public BaseResponse<Boolean> setFingerPrint(String fingerPrint){
        ThrowUtils.throwIf(StrUtil.isBlank(fingerPrint), ErrorCode.PARAMS_ERROR, "权限参数为空");
        Fingerprint fingerprint = new Fingerprint();
        fingerprint.setFingerprint(fingerPrint);
        long count = fingerprintService.count(new QueryWrapper<Fingerprint>().eq("fingerprint", fingerPrint));
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "此设备已授权,请勿重复授权");
        return ResultUtils.success(fingerprintService.save(fingerprint));
    }
    @GetMapping("/check")
    public BaseResponse<Boolean> checkFingerPrint(String fingerPrint){
        ThrowUtils.throwIf(StrUtil.isBlank(fingerPrint), ErrorCode.PARAMS_ERROR, "权限参数为空");
        long count = fingerprintService.count(new QueryWrapper<Fingerprint>().eq("fingerprint", fingerPrint));
        ThrowUtils.throwIf(count <= 0, ErrorCode.PARAMS_ERROR, "此设备没有授权,无权限访问");
        return ResultUtils.success(true);
    }

}
