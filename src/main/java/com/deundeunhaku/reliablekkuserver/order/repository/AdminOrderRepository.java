package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminOrderRepository extends JpaRepository<Order, Long>,
        AdminOrderRepositoryCustom {
}
