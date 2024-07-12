package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrder;
import com.deundeunhaku.reliablekkuserver.order.domain.MenuOrderId;
import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuOrderRepository extends JpaRepository<MenuOrder, MenuOrderId>, MenuOrderRepositoryCustom {

    List<MenuOrder> findByOrder(Order order);

}
