package com.ninja.wangjia_backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ninja.wangjia_backend.annotation.AuthCheck;
import com.ninja.wangjia_backend.common.BaseResponse;
import com.ninja.wangjia_backend.common.DeleteRequest;
import com.ninja.wangjia_backend.common.ResultUtils;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.room.RoomAddRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomQueryRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomSetStateRequest;
import com.ninja.wangjia_backend.model.dto.room.RoomUpdateRequest;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.service.RoomService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Resource
    private RoomService roomService;
    @PostMapping("/set/state")
    @AuthCheck(mustRole = "user")
    public BaseResponse<Boolean> setRoomState(@RequestBody RoomSetStateRequest roomSetStateRequest) {
        ThrowUtils.throwIf(roomSetStateRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(roomSetStateRequest.getId() == null, ErrorCode.PARAMS_ERROR, "id为空");
        //房间不存在
        long count = roomService.count(new QueryWrapper<Room>().eq("id", roomSetStateRequest.getId()));
        ThrowUtils.throwIf(count <= 0, ErrorCode.PARAMS_ERROR, "房间不存在");
        Room room = new Room();
        BeanUtils.copyProperties(roomSetStateRequest, room);
        return ResultUtils.success(roomService.updateById(room));
    }

    /*
    * 以下都是对于房间的常规CRUD操作
    * */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addRoom(@RequestBody RoomAddRequest roomAddRequest) {
        ThrowUtils.throwIf(roomAddRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
        ThrowUtils.throwIf(roomAddRequest.getRoomId() == null, ErrorCode.PARAMS_ERROR, "参数为空");
        //房间id不能相同
        long count = roomService.count(new QueryWrapper<Room>().eq("roomId", roomAddRequest.getRoomId()));
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "房间已存在");
        Room room = new Room();
        BeanUtils.copyProperties(roomAddRequest, room);
        boolean result = roomService.save(room);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "添加失败");
        return ResultUtils.success(room.getId());
    }
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteRoom(@RequestBody DeleteRequest deleteRequest) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        boolean b = roomService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateRoom(@RequestBody RoomUpdateRequest roomUpdateRequest) {
        ThrowUtils.throwIf(roomUpdateRequest == null || roomUpdateRequest.getId() == null, ErrorCode.PARAMS_ERROR);
        long count = roomService.count(new QueryWrapper<Room>().eq("roomId", roomUpdateRequest.getRoomId()));
        ThrowUtils.throwIf(count > 0 && !roomUpdateRequest.getRoomId().equals(roomService.getById(roomUpdateRequest.getId()).getRoomId()), ErrorCode.OPERATION_ERROR, "房间已存在");
        Room room = new Room();
        BeanUtils.copyProperties(roomUpdateRequest, room);
        boolean result = roomService.updateById(room);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");
        return ResultUtils.success(true);
    }
    @PostMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<Room>> listRoomByPage(@RequestBody RoomQueryRequest roomQueryRequest) {
        ThrowUtils.throwIf(roomQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = roomQueryRequest.getCurrent();
        long pageSize = roomQueryRequest.getPageSize();
        Page<Room> roomPage = roomService.page(new Page<>(current, pageSize),roomService.getQueryWrapper(roomQueryRequest));
        return ResultUtils.success(roomPage);
    }
    @PostMapping("/list")
    @AuthCheck(mustRole = "user")
    public BaseResponse<List<Room>> listRoom() {
        return ResultUtils.success(roomService.list());
    }
}
