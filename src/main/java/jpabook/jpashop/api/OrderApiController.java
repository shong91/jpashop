package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;

  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {
    // V1. 엔티티 직접 노출 (비권장)
    List<Order> all = orderRepository.findAllByString(new OrderSearch());

    // LAZY 강제 초기화
    for (Order order : all) {
      order.getMember().getName();
      order.getDelivery().getAddress();

      List<OrderItem> orderItems = order.getOrderItems();
      orderItems.stream().forEach(o -> o.getItem().getName());
    }
    return all;
  }

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    // V2: 엔티티를 DTO 로 변환

    List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    return result;
  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    // V3: 엔티티를 DTO 로 변환 - fetch join 최적화
    List<Order> orders = orderRepository.findAllWithItem();
    List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    return result;
  }

  @Data
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;
    // DTO 안에 엔티티가 wrapping 되어있어서는 안됨! 엔티티와의 의존관계를 모두 끊어주어야 함.
    private List<OrderItemDto> orderItems;


    public OrderDto(Order order) {
      this.orderId = order.getId();
      this.name = order.getMember().getName();
      this.orderDate = order.getOrderDate();
      this.orderStatus = order.getStatus();
      this.address = order.getMember().getAddress();
      this.orderItems = order.getOrderItems().stream().map(orderItem -> new OrderItemDto(orderItem))
          .collect(
              Collectors.toList());

    }
  }

  @Getter
  static class OrderItemDto {

    private String itemName;
    private int orderPrice;
    private int count;

    public OrderItemDto(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }
}
