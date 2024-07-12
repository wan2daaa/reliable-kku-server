package com.deundeunhaku.reliablekkuserver.order.repository;

import com.deundeunhaku.reliablekkuserver.order.domain.Order;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.QOrderEachMenuResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.deundeunhaku.reliablekkuserver.menu.domain.QMenu.menu;
import static com.deundeunhaku.reliablekkuserver.order.domain.QMenuOrder.menuOrder;

@RequiredArgsConstructor
public class MenuOrderRepositoryImpl implements MenuOrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderEachMenuResponse> findByOrderToOrderEachMenuResponse(Order order) {
        return queryFactory
                .select(
                        new QOrderEachMenuResponse(menu.name, menuOrder.count)
                ).from(menuOrder)
                .innerJoin(menu).on(menuOrder.menu.id.eq(menu.id))
                .where(menuOrder.order.eq(order))
                .fetch();
    }

    @Override
    public List<String> findMenuNameByEachMenuOrder(Order order) {
        return queryFactory
                .select(
                        menu.name
                ).from(menuOrder)
                .innerJoin(menu).on(menuOrder.menu.id.eq(menu.id))
                .where(menuOrder.order.eq(order))
                .fetch();
    }
}
