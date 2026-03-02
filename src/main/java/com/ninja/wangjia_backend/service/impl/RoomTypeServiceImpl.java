package com.ninja.wangjia_backend.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ninja.wangjia_backend.exception.ErrorCode;
import com.ninja.wangjia_backend.exception.ThrowUtils;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeAddRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeQueryRequest;
import com.ninja.wangjia_backend.model.dto.room_type.RoomTypeUpdateRequest;
import com.ninja.wangjia_backend.model.entity.RoomType;
import com.ninja.wangjia_backend.service.RoomTypeService;
import com.ninja.wangjia_backend.mapper.RoomTypeMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【room_type(房间类型)】的数据库操作Service实现
* @createDate 2026-03-01 23:50:38
*/
@Service
public class RoomTypeServiceImpl extends ServiceImpl<RoomTypeMapper, RoomType>
    implements RoomTypeService{

    @Override
    public long addRoomType(RoomTypeAddRequest roomTypeAddRequest) {
        ThrowUtils.throwIf(roomTypeAddRequest == null, ErrorCode.PARAMS_ERROR,"参数为空");
        ThrowUtils.throwIf(roomTypeAddRequest.getName() == null || roomTypeAddRequest.getPrice() == null || roomTypeAddRequest.getPeopleNum() == null, ErrorCode.PARAMS_ERROR,"输入不能为空");
        //如果房间类型已存在
        long count = this.count(new QueryWrapper<RoomType>().eq("name", roomTypeAddRequest.getName()));
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR,"房间类型已存在");

        RoomType roomType = new RoomType();
        BeanUtils.copyProperties(roomTypeAddRequest, roomType);
        boolean result = this.save(roomType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"添加房间类型失败");
        return roomType.getId();
    }

    @Override
    public Boolean updateRoomType(RoomTypeUpdateRequest roomTypeUpdateRequest) {
        ThrowUtils.throwIf(roomTypeUpdateRequest == null, ErrorCode.PARAMS_ERROR,"参数为空");
        RoomType roomType = new RoomType();
        BeanUtils.copyProperties(roomTypeUpdateRequest, roomType);
        boolean result = this.updateById(roomType);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"更新房间类型失败");
        return true;
    }

    @Override
    public Wrapper<RoomType> getQueryWrapper(RoomTypeQueryRequest roomTypeQueryRequest) {
        ThrowUtils.throwIf(roomTypeQueryRequest == null, ErrorCode.PARAMS_ERROR,"请求参数为空");
        Long id = roomTypeQueryRequest.getId();
        String sortField = roomTypeQueryRequest.getSortField();
        String sortOrder = roomTypeQueryRequest.getSortOrder();
        QueryWrapper<RoomType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
        queryWrapper.orderBy(ObjUtil.isNotNull(sortField), sortOrder.equals("ascend"), sortField);
        return queryWrapper;
    }

}




