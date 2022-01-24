package com.statemachine.sample.service;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderSupplierType;
import com.statemachine.sample.constants.OrderType;
import com.statemachine.sample.domain.OrderEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {

    private static Map<String, OrderEntry> orderRepository = new HashMap<>();

    public OrderEntry getById(String orderId) throws Exception {
        return orderRepository.get(orderId);
    }


    @PostConstruct
    public void init(){
        orderRepository.put("CREATE_NORMAL_JIT", getOrderEntry("CREATE_NORMAL_JIT", OrderStatus.CREATED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("CREATE_NORMAL_ON_HAND", getOrderEntry("CREATE_NORMAL_ON_HAND", OrderStatus.CREATED, OrderType.NORMAL, OrderSupplierType.ON_HAND));
        orderRepository.put("SHIPPED_NORMAL_JIT", getOrderEntry("SHIPPED_NORMAL_JIT", OrderStatus.SHIPPED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("PACKED_NORMAL_JIT", getOrderEntry("PACKED_NORMAL_JIT", OrderStatus.PACKED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("DELIVERED_NORMAL_ON_HAND", getOrderEntry("DELIVERED_NORMAL_ON_HAND", OrderStatus.DELIVERED, OrderType.NORMAL, OrderSupplierType.ON_HAND));
        orderRepository.put("CREATE_PRIORITY_JIT", getOrderEntry("CREATE_PRIORITY_JIT", OrderStatus.CREATED, OrderType.PRIORITY, OrderSupplierType.JIT));
    }

    private OrderEntry getOrderEntry(String orderId, OrderStatus status, OrderType type, OrderSupplierType supplierType) {
        OrderEntry entry = new OrderEntry();
        entry.setOrderSupplierType(supplierType);
        entry.setOrderId(orderId);
        entry.setStatus(status);
        entry.setRemarks("order remarks");
        entry.setLastUpdatedBy("Operations");
        return entry;
    }


}
