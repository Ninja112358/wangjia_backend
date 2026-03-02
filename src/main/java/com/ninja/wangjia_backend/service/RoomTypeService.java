package com.ninja.wangjia_backend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeAddRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeQueryRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeUpdateRequest;
import com.ninja.wangjia_backend.model.entity.RoomType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【room_type(房间类型)】的数据库操作Service
* @createDate 2026-03-01 23:50:38
*/
public interface RoomTypeService extends IService<RoomType> {
    long addRoomType(RoomTypeAddRequest roomTypeAddRequest);
    Boolean updateRoomType(RoomTypeUpdateRequest roomTypeUpdateRequest);
    Wrapper<RoomType> getQueryWrapper(RoomTypeQueryRequest roomTypeQueryRequest);
}
