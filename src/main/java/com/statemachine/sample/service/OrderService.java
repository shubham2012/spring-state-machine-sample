package com.statemachine.sample.service;

import com.statemachine.sample.constants.OrderStatus;
import com.statemachine.sample.constants.OrderSupplierType;
import com.statemachine.sample.constants.OrderType;
import com.statemachine.sample.domain.OrderEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OrderService {

    private static Map<String, OrderEntry> orderRepository = new HashMap<>();

    /**
     * Get order by order id
     * @param orderId
     * @return
     */
    public OrderEntry getById(String orderId) {
        log.debug("Retrieving order: {}", orderId);
        return orderRepository.get(orderId);
    }


    //-------------------------------- Local repository -----------------------------------------------------

    /**
     * Having local repository to avoid DB calls and setup for sample state machine
     */
    @PostConstruct
    public void init() {
        orderRepository.put("CREATE_NORMAL_JIT", getOrderEntry("CREATE_NORMAL_JIT", OrderStatus.CREATED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("CREATE_NORMAL_ON_HAND", getOrderEntry("CREATE_NORMAL_ON_HAND", OrderStatus.CREATED, OrderType.NORMAL, OrderSupplierType.ON_HAND));
        orderRepository.put("SHIPPED_NORMAL_JIT", getOrderEntry("SHIPPED_NORMAL_JIT", OrderStatus.SHIPPED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("PACKED_NORMAL_JIT", getOrderEntry("PACKED_NORMAL_JIT", OrderStatus.PACKED, OrderType.NORMAL, OrderSupplierType.JIT));
        orderRepository.put("DELIVERED_NORMAL_ON_HAND", getOrderEntry("DELIVERED_NORMAL_ON_HAND", OrderStatus.DELIVERED, OrderType.NORMAL, OrderSupplierType.ON_HAND));
        orderRepository.put("CREATE_PRIORITY_JIT", getOrderEntry("CREATE_PRIORITY_JIT", OrderStatus.CREATED, OrderType.PRIORITY, OrderSupplierType.JIT));
        orderRepository.put("OUT_FOR_DELIVERY_NORMAL_ON_HAND", getOrderEntry("OUT_FOR_DELIVERY_NORMAL_ON_HAND", OrderStatus.OUT_FOR_DELIVERY, OrderType.NORMAL, OrderSupplierType.ON_HAND));
        orderRepository.put("PROCESSING_NORMAL_ON_HAND", getOrderEntry("OUT_FOR_DELIVERY_NORMAL_ON_HAND", OrderStatus.PROCESSING, OrderType.NORMAL, OrderSupplierType.ON_HAND));
    }

    /**
     * Internal method to provide get the order entry
     * @param orderId
     * @param status
     * @param type
     * @param supplierType
     * @return
     */
    private OrderEntry getOrderEntry(String orderId, OrderStatus status, OrderType type, OrderSupplierType supplierType) {
        OrderEntry entry = new OrderEntry();
        entry.setOrderSupplierType(supplierType);
        entry.setId(orderId);
        entry.setStatus(status);
        entry.setRemarks("order remarks");
        entry.setLastUpdatedBy("Operations");
        return entry;
    }


}
