package com.ninja.wangjia_backend.model.dto.room;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomCheckInRequest implements Serializable {
    private String roomId;
    private Double roomPrice;
    private String roomType;
    private String roomPeopleNum;
}
