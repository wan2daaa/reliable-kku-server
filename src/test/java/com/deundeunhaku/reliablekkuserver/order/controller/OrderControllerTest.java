package com.deundeunhaku.reliablekkuserver.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.deundeunhaku.reliablekkuserver.BaseControllerTest;
import com.deundeunhaku.reliablekkuserver.order.dto.LeftTimeResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderCalendarResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderEachMenuResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderIdResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderRegisterRequest;
import com.deundeunhaku.reliablekkuserver.order.dto.OrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.PastOrderResponse;
import com.deundeunhaku.reliablekkuserver.order.dto.RegisteredMenuRequest;
import com.deundeunhaku.reliablekkuserver.order.service.OrderService;
import com.deundeunhaku.reliablekkuserver.payment.service.PaymentService;
import com.deundeunhaku.reliablekkuserver.sse.service.SseService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.ResultActions;

class OrderControllerTest extends BaseControllerTest {

  @MockBean
  private OrderService orderService;

  @MockBean
  private SseService sseService;

  @MockBean
  private PaymentService paymentService;


  @Test
  void 유저의_주문을_등록한다() throws Exception {
    //given
    RegisteredMenuRequest menu1 = RegisteredMenuRequest.of(
        1L,
        3
    );
    RegisteredMenuRequest menu2 = RegisteredMenuRequest.of(
        2L,
        5
    );

    OrderRegisterRequest request = OrderRegisterRequest.of(
        "tossOrderId",
        10000,
        List.of(menu1, menu2)
    );

    when(orderService.registerOrder(any(), any()))
        .thenReturn(
            OrderIdResponse.of(1L)
        );

    //when
    ResultActions resultActions = mockMvc.perform(post(API + "/order")
            .contentType(APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andDo(print());

    //then
    resultActions.andExpect(status().isCreated())
        .andDo(document("order/register/success",
            requestFields(
                fieldWithPath("tossOrderId").description("토스 주문번호"),
                fieldWithPath("orderPrice").description("총 가격"),
                fieldWithPath("registeredMenus[].menuId")
                    .description("각 메뉴 아이디"),
                fieldWithPath("registeredMenus[].count").description("각 메뉴 개수")
            ),
            responseFields(
                fieldWithPath("id").description("주문 아이디")
            )
        ));
  }

  @Test
  void 주문의_메뉴_리스트를_반환한다() throws Exception {
    //given
    Long orderId = 1L;

    OrderEachMenuResponse 팥_붕어빵 = OrderEachMenuResponse.of("팥 붕어빵", 2);
    OrderEachMenuResponse 슈크림_붕어빵 = OrderEachMenuResponse.of("슈크림 붕어빵", 4);

    when(orderService.getOrderMenuList(anyLong(), any()))
        .thenReturn(OrderResponse.of("이름", 15000, List.of(팥_붕어빵, 슈크림_붕어빵)));

    //when
    ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API + "/order/{orderId}", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/get/success",
            pathParameters(
                parameterWithName("orderId").description("주문 ID")
            ),
            responseFields(
                fieldWithPath("username").description("유저의 이름"),
                fieldWithPath("totalPrice").description("총 가격"),
                fieldWithPath("orderMenuList[].name").description("메뉴 이름"),
                fieldWithPath("orderMenuList[].count").description("메뉴 개수")
            )
        ));

  }

  @Test
  void 주문을_취소한다() throws Exception {
    //given
    Long orderId = 1L;

    //when
    ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.delete(API + "/order/{orderId}", orderId))
        .andDo(print());

    //then
    resultActions.andExpect(status().isNoContent())
        .andDo(document("order/delete/success",
            pathParameters(
                parameterWithName("orderId").description("주문 ID")
            )
        ));
  }

  @Test
  void 주문이_현재_진행중인지_확인해준다() throws Exception {
    //given
    when(orderService.isMemberNowOrdered(any()))
        .thenReturn(OrderIdResponse.of(1L));

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/order/duplicate"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/duplicate/success",
            responseFields(
                fieldWithPath("id").description("주문 아이디")
            )
        ));

  }

  @Test
  void 주문이_만약_현재진행중이아니면_에러를던진다() throws Exception {
    //given
    when(orderService.isMemberNowOrdered(any()))
        .thenThrow(new IllegalArgumentException("주문이 진행중이지 않습니다."));

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/order/duplicate"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isBadRequest())
        .andDo(document("order/duplicate/fail",
            responseFields(
                fieldWithPath("message").description("에러 메세지")
            )
        ));

  }

  @Test
  void 년도와_월을_받아_메뉴룰_주문한날에_도장이_달력에_찍혀있는지_확인한다() throws Exception {

    //given
    OrderCalendarResponse response1 = OrderCalendarResponse.of(21, true);
    OrderCalendarResponse response2 = OrderCalendarResponse.of(23, true);
    OrderCalendarResponse response3 = OrderCalendarResponse.of(25, false);
    Integer year = 2023;
    Integer month = 10;

    when(orderService.getOrderListByMemberAndYearAndMonth(any(), any(Integer.class),
        any(Integer.class))).thenReturn(List.of(response1, response2, response3));

    //when
    ResultActions resultActions = mockMvc.perform(
            RestDocumentationRequestBuilders.get(API + "/order/calendar")
                .contentType(MediaType.APPLICATION_JSON)
                .param("year", String.valueOf(year))
                .param("month", String.valueOf(month)))
        .andDo(print());
    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/calendar/success",
            queryParameters(
                parameterWithName("year").description("주문 년도"),
                parameterWithName("month").description("주문 월")
            ),
            responseFields(
                fieldWithPath("[].orderedDay").description("주문한 날짜"),
                fieldWithPath("[].isOrdered").description("주문여부")
            )
        ));
  }

  @Test
  void 과거의_주문_리스트를_반환한다() throws Exception {
    //given
    OrderEachMenuResponse eachOrderResponse1 = OrderEachMenuResponse.of("후라이드", 1);
    OrderEachMenuResponse eachOrderResponse2 = OrderEachMenuResponse.of("양념", 2);
    OrderEachMenuResponse eachOrderResponse3 = OrderEachMenuResponse.of("간장", 3);

    PastOrderResponse response1 = PastOrderResponse.of(LocalDate.of(2023, 10, 29),
        LocalTime.of(12, 30, 30),
        1000,
        List.of(eachOrderResponse1, eachOrderResponse2, eachOrderResponse3));

    PastOrderResponse response2 = PastOrderResponse.of(LocalDate.of(2023, 10, 27),
        LocalTime.of(4, 28, 30),
        1200,
        List.of(eachOrderResponse1, eachOrderResponse2, eachOrderResponse3));

    when(orderService.getPastOrderList(any()))
        .thenReturn(List.of(response1, response2));

    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/order/past"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/past/success",
            responseFields(
                fieldWithPath("[].day").description("주문 일자"),
                fieldWithPath("[].dayOfTheWeek").description("주문 날짜"),
                fieldWithPath("[].orderedTime").description("주문 시간"),
                fieldWithPath("[].totalPrice").description("총 가격"),
                fieldWithPath("[].totalCount").description("총 주문 수량"),
                fieldWithPath("[].orderMenuList[].name").description("메뉴 명"),
                fieldWithPath("[].orderMenuList[].count").description("메뉴 별 주문 수량")
            )
        ));
  }

  @Test
  void 현재_주문하면_걸리는시간을_반환한다() throws Exception {
    //given

    when(orderService.getLeftTime())
        .thenReturn(LeftTimeResponse.of(Duration.ofMinutes(30).toMinutes()));
    //when
    ResultActions resultActions = mockMvc.perform(get(API + "/order/left-time"))
        .andDo(print());

    //then
    resultActions.andExpect(status().isOk())
        .andDo(document("order/left-time/success",
            responseFields(
                fieldWithPath("leftMinutes").description("예상 걸리는 시간 (분단위)")
            )
        ));

  }
}