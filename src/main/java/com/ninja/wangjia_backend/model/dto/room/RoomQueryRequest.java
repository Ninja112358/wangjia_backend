package com.ninja.wangjia_backend.model.dto.room;

import com.ninja.wangjia_backend.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoomQueryRequest extends PageRequest implements Serializable {
    private Long id;
    private static final long serialVersionUID = 1L;
}
