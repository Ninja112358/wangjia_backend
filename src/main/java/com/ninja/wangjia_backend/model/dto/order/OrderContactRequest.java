package com.ninja.wangjia_backend.model.dto.order;

import com.ninja.wangjia_backend.model.entity.OrderGroup;
import com.ninja.wangjia_backend.model.vo.OrderGroupSelectInfoVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OrderContactRequest implements Serializable {
    private Long orderGroupId;
    private List<OrderGroupSelectInfoVO> orderGroupSelectInfoList;
}
