package com.deundeunhaku.reliablekkuserver.order.service;

import com.deundeunhaku.reliablekkuserver.order.constant.OrderStatus;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.AdminOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.repository.MenuOrderRepository;
import com.deundeunhaku.reliablekkuserver.order.repository.OrderRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminOrderService {

  private final OrderRepository orderRepository;
  private final MenuOrderRepository menuOrderRepository;

  public List<AdminOrderResponse> getOrderList(OrderStatus orderStatus) {
    List<Order> orderList = orderRepository.findByOrderStatusOrderByOrderDatetimeAsc(
        orderStatus);

    return orderList.stream()
        .map(order ->
            AdminOrderResponse.of(
                order.getTodayOrderCount(),
                order.getMember().getPhoneNumber(),
                order.getOrderDatetime().toLocalTime(),
                order.getIsOfflineOrder(),
                menuOrderRepository.findByOrderToOrderEachMenuResponse(order)
            )).toList();
  }
}
