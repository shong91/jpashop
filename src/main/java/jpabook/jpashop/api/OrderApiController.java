package jpabook.jpashop.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

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
    // 네트워킹은 1번만에 모든 데이터를 가져올 수 있으나, 데이터 중복 발생 가능성
    // 페이징 불가

    List<Order> orders = orderRepository.findAllWithItem();
    List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    return result;
  }

  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> ordersV3_page(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    // V3.1: 엔티티를 DTO 로 변환 - 페이징 한계 돌파
    // v3 대비 네트워킹은 더 일어나나, 정규화된 상태의 데이터를 조회할 수 있다.
    // 페이징 가능

    // 1. *ToOne 관계는 모두 fetch join
    List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

    // 2. *ToMany 관계(컬렉션) 은 지연 로딩으로 조회한다. (hibernate.default_batch_fetch_size 사용)
    List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(Collectors.toList());
    return result;
  }

  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findOrderQueryDtos();
  }

  @GetMapping("/api/v5/orders")
  public List<OrderQueryDto> ordersV5() {
    return orderQueryRepository.findAllByDto_optimization();
  }

  @GetMapping("/api/v6/orders")
  public List<OrderQueryDto> ordersV6() {
    // V6: JPA에서 DTO로 직접 조회 - 플랫 데이터 최적화
    // 쿼리 1번으로 조회 가능
    // 애플리케이션에서 추가 작업 필요: orderFlatDto -> OrderQueryDto 로 매핑하며 중복 제거
    // 페이징 불가

    List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

    return flats.stream()
        .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(),
                o.getAddress(), o.getOrderItems()),
            mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(),
                o.getCount()), Collectors.toList())
        )).entrySet().stream()
        .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(),
            e.getKey().getOrderDate(), e.getKey().getAddress(),
            e.getValue()))
        .collect(Collectors.toList());
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
