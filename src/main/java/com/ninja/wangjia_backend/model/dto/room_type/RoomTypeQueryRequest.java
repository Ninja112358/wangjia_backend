package com.ninja.wangjia_backend.model.dto.room_type;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoomTypeQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private static final long serialVersionUID = 1L;
}
