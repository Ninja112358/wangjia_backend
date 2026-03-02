package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.DeleteRequest;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeAddRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeQueryRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeUpdateRequest;
import com.ninja.wangjia_backend.model.entity.RoomType;
import com.ninja.wangjia_backend.service.RoomTypeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("room_type")
public class RoomTypeController {
    @Resource
    private RoomTypeService roomTypeService;
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addRoomType(@RequestBody RoomTypeAddRequest roomTypeAddRequest) {
        return ResultUtils.success(roomTypeService.addRoomType(roomTypeAddRequest));
    }
    @PostMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<RoomType>> listRoomTypeByPage(@RequestBody RoomTypeQueryRequest roomTypeQueryRequest) {
        ThrowUtils.throwIf(roomTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = roomTypeQueryRequest.getCurrent();
        long pageSize = roomTypeQueryRequest.getPageSize();
        Page<RoomType> roomTypePage = roomTypeService.page(new Page<>(current, pageSize),roomTypeService.getQueryWrapper(roomTypeQueryRequest));
        return ResultUtils.success(roomTypePage);
    }
    @PostMapping("/list")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<List<RoomType>> listRoomType() {
        return ResultUtils.success(roomTypeService.list());
    }
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteRoomType(@RequestBody DeleteRequest deleteRequest) {
        return ResultUtils.success(roomTypeService.removeById(deleteRequest.getId()));
    }
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateRoomType(@RequestBody RoomTypeUpdateRequest roomTypeUpdateRequest) {
        return ResultUtils.success(roomTypeService.updateRoomType(roomTypeUpdateRequest));
    }
}
