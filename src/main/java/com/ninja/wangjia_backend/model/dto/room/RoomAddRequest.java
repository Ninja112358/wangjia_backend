package com.ninja.wangjia_backend.model.dto.room;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomAddRequest implements Serializable {
    private String roomId;
    private Integer roomFloor;
    private String roomType;
    private Integer roomPeopleNum;
    private Double roomPrice;
    private String roomInfo;
    private String isWindow;
    private static final long serialVersionUID = 1L;
}
