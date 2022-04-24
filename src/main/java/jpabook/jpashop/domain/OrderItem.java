package jpabook.jpashop.domain;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderItem {

  @Id
  @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  private int orderPrice;

  private int count;

  // 생성 메서드
  public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
    OrderItem orderItem = new OrderItem();
    orderItem.setItem(item);
    orderItem.setOrderPrice(orderPrice);
    orderItem.setCount(count);

    // 재고 수량
    item.removeStock(count);

    return orderItem;
  }

  // 비즈니스 로직
  public void cancel() {
    // 개별 주문에 대해 재고 수량 원복
    getItem().addStock(count);
  }

  // 조회 로직

  /**
   * 주문 상품 전체 가격조회
   *
   * @return
   */
  public int getTotalPrice() {
    return getOrderPrice() * getCount();
  }
}
