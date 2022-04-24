package jpabook.jpashop.domain;

import static javax.persistence.FetchType.LAZY;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

  @Id
  @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();

  @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "delivery_id")
  private Delivery delivery;

  private LocalDateTime orderDate;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;

  // 연관관계 메서드
  // 양방향 연관관계에 있는 엔티티들은, 한쪽 엔티티가 변경되면 다른 한쪽도 함께 변경 내용이 영속화되어야 한다.
  public void setMember(Member member) {
    this.member = member;
    member.getOrders().add(this);
  }

  public void addOrderItem(OrderItem orderItem) {
    orderItems.add(orderItem);
    orderItem.setOrder(this);

  }

  public void setDelivery(Delivery delivery) {
    this.delivery = delivery;
    delivery.setOrder(this);
  }

  // 생성 메서드
  // 생성 할 때 모든 값들을 set 하고, 주문 생성에 대한 비즈니스 로직을 "완성" 시킴!
  public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
    Order order = new Order();
    order.setMember(member);
    order.setDelivery(delivery);
    for (OrderItem orderItem : orderItems) {
      order.addOrderItem(orderItem);
    }
    order.setStatus(OrderStatus.ORDER);
    order.setOrderDate(LocalDateTime.now());

    return order;
  }

  // 비즈니스 로직

  /**
   * 주문 취소
   */
  public void cancel() {
    // 비즈니스 로직에 대한 체크 로직이 엔티티 안에 존재하도록 함
    if (delivery.getStatus() == DeliveryStatus.COMP) {
      throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가합니다.");
    }

    this.setStatus(OrderStatus.CANCEL);
    for (OrderItem orderItem : orderItems) {
      orderItem.cancel();
    }
  }

  // 조회 로직 

  /**
   * 전체 주문 가격 조회
   *
   * @return
   */
  public int getTotalPrice() {
    // thanks to IntelliJ ! for loop to lambda stream
    int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
    return totalPrice;
  }
}
