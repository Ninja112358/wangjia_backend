package com.ninja.wangjia_backend.model.dto.order;

import com.ninja.wangjia_backend.model.dto.room.RoomCheckInRequest;
import com.ninja.wangjia_backend.model.entity.Room;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderCheckInRequest implements Serializable {
    private String name;
    private String phone;
    private String idCard;
    private String cardType;
    private String orderInfo;
    private Integer customType;
    private Double pay;
    private List<RoomCheckInRequest> roomList;





    private static final long serialVersionUID = 1L;
}
