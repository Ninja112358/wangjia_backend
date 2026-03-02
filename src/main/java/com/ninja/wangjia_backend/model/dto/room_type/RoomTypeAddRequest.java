package com.ninja.wangjia_backend.model.dto.room_type;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomTypeAddRequest implements Serializable {
    private String name;
    private Double price;
    private Integer peopleNum;
    private static final long serialVersionUID = 1L;
}
