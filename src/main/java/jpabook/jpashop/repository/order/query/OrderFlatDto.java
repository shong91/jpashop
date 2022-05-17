package jpabook.jpashop.repository.order.query;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import lombok.Data;

@Data
public class OrderFlatDto {

  // OrderQueryDto
  private Long orderId;
  private String name;
  private LocalDateTime orderDate;
  private Address address;
  private List<OrderItemQueryDto> orderItems;

  // OrderItemQueryDto
  private String itemName;
  private int orderPrice;
  private int count;

  public OrderFlatDto(Long orderId, String name, LocalDateTime orderDate,
      Address address,
      List<OrderItemQueryDto> orderItems, String itemName, int orderPrice, int count) {
    this.orderId = orderId;
    this.name = name;
    this.orderDate = orderDate;
    this.address = address;
    this.orderItems = orderItems;
    this.itemName = itemName;
    this.orderPrice = orderPrice;
    this.count = count;
  }
}
