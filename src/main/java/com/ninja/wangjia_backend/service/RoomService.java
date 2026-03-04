package com.ninja.wangjia_backend.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ninja.wangjia_backend.model.dto.room.RoomQueryRequest;
import com.ninja.wangjia_backend.model.entity.Room;

/**
* @author Administrator
* @description 针对表【room(房间)】的数据库操作Service
* @createDate 2026-03-02 13:48:01
*/
public interface RoomService extends IService<Room> {

    Wrapper<Room> getQueryWrapper(RoomQueryRequest roomQueryRequest);

}
