package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * xToOne(ManyToOne, OneToOne) 관계에서의 성능 최적화
 * <p>
 * Order
 * <p>
 * Order -> Member
 * <p>
 * Order -> Delivery
 * <p>
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

  private final OrderRepository orderRepository;

  private final OrderSimpleQueryRepository orderSimpleQueryRepository;

  @GetMapping("/api/v1/simple-orders")
  public List<Order> ordersV1() {
    // V1: 엔티티를 직접 노출

    // 엔티티를 직접 노출하지 말 것! 성능에도 영향.
    // 문제 1. 무한루프: 양방향 연관관계에 있는 두 엔티티 간 한쪽은 @JsonIgnore 를 걸어주어야 방지.
    // 문제 2. 지연로딩 시 프록시 객체(proxy.bytebuddy)를 가져오게 하기 위해 hibernate5Module 사용
    // or 아래 코드처럼 LAZY 강제 초기화 필요
    // 문제 3. API spec 상 필요 없는 정보까지 노출하게 됨.

    List<Order> all = orderRepository.findAllByString(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName(); // LAZY 강제 초기화 -> member query 를 날려 값을 가져오게 됨
      order.getDelivery().getAddress();
    }
    return all;

  }

  @GetMapping("/api/v2/simple-orders")
  public List<SimpleOrderDto> ordersV2() {
    // V2: 엔티티를 DTO 로 변환

    // N+1 문제
    // 기본 쿼리 1 + 회원 N + 배송 N
    // => order 건 수 만큼 loop 가 돌 때, order -> member -> delivery 순으로 쿼리를 반복 호출하게 됨
    // ex 오더가 2건일 때, 최악의 경우 총 5개의 쿼리가 나가게 됨
    // (1(오더) + 1(오더1의 회원) + 1(오더1의 배송) +1(오더2의 회원) + 1(오더2의 배송))
    // 왜 "최악의 경우"?: 지연 로딩은 영속성 컨텍스트에서 조회하므로, 이미 조회된 경우 쿼리를 생략한다.

    List<Order> orders = orderRepository.findAllByString(new OrderSearch());
    List<SimpleOrderDto> result = orders.stream().map(SimpleOrderDto::new)
        .collect(Collectors.toList());

    return result;
  }

  @GetMapping("/api/v3/simple-orders")
  public List<SimpleOrderDto> orderV3() {
    // V3: 엔티티를 DTO 로 변환 - fetch join 최적화

    // fetch join 으로 order 조회 시 member, delivery 의 데이터를 한 번에 가져옴 (모든 필드 조회)
    // => 지연로딩 자체가 일어나지 않음.
    // V4 대비 DB -> 애플리케이션 네트워킹이 많이 일어나나, 재사용성 높음

    List<Order> orders = orderRepository.findAllWithMemberDelivery();
    return orders.stream().map(SimpleOrderDto::new)
        .collect(Collectors.toList());

  }

  @Data
  static class SimpleOrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public SimpleOrderDto(Order order) {
      this.orderId = order.getId();
      this.name = order.getMember().getName(); // LAZY 초기화 -> member query 를 날려 값을 가져오게 됨
      this.orderDate = order.getOrderDate();
      this.orderStatus = order.getStatus();
      this.address = order.getMember().getAddress(); // LAZY 초기화 -> delivery query 를 날려 값을 가져오게 됨
    }
  }

}
