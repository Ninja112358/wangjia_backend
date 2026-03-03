package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.room.RoomQueryRequest;
import com.ninja.wangjia_backend.model.entity.Room;
import com.ninja.wangjia_backend.model.entity.RoomType;
import com.ninja.wangjia_backend.service.RoomService;
import com.ninja.wangjia_backend.mapper.RoomMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【room(房间)】的数据库操作Service实现
* @createDate 2026-03-02 13:48:01
*/
@Service
public class RoomServiceImpl extends ServiceImpl<RoomMapper, Room>
    implements RoomService{

    @Override
    public Wrapper<Room> getQueryWrapper(RoomQueryRequest roomQueryRequest) {
        ThrowUtils.throwIf(roomQueryRequest == null, ErrorCode.PARAMS_ERROR,"请求参数为空");
        Long id = roomQueryRequest.getId();
        String sortField = roomQueryRequest.getSortField();
        String sortOrder = roomQueryRequest.getSortOrder();
        QueryWrapper<Room> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.orderBy(ObjUtil.isNotNull(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }
}




