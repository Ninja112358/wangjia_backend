package com.ninja.wangjia_backend.model.dto.room;

import lombok.Data;

import java.io.Serializable;

@Data
public class RoomSetStateRequest implements Serializable {
    private Long id;
    private Integer roomState;
    private static final long serialVersionUID = 1L;
}
