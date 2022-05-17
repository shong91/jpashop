package jpabook.jpashop.repository.order.query;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  public List<OrderQueryDto> findOrderQueryDtos() {
    // root query 1번
    List<OrderQueryDto> result = findOrders();

    // collection N번 실행
    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
      o.setOrderItems(orderItems);
    });

    return result;
  }

  public List<OrderQueryDto> findAllByDto_optimization() {
    // root query 1번
    List<OrderQueryDto> result = findOrders();
    // collection query 1번
    List<Long> orderIds = toOrderIds(result);
    Map<Long, List<OrderItemQueryDto>> orderItemMap = findOrderItemMap(
        orderIds);

    // 메모리에서 값 매핑 (O(1) 으로 성능 최적화)
    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  public List<OrderFlatDto> findAllByDto_flat() {
    // query 1번
    return em.createQuery("select new "
        + "jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) "
        + "from Order o "
        + "join o.member m "
        + "join o.delivery d "
        + "join o.orderItems oi "
        + "join oi.item i ", OrderFlatDto.class).getResultList();
  }

  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
            + "from OrderItem oi "
            + "join oi.item i "
            + "where oi.order.id = :orderId ", OrderItemQueryDto.class)
        .setParameter("orderId", orderId)
        .getResultList();
  }

  public List<OrderQueryDto> findOrders() {
    return em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
            + " from Order o " + "join o.member m " + "join o.delivery d ",
        OrderQueryDto.class).getResultList();
  }

  private List<Long> toOrderIds(List<OrderQueryDto> result) {
    List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());
    return orderIds;
  }

  private Map<Long, List<OrderItemQueryDto>> findOrderItemMap(List<Long> orderIds) {
    List<OrderItemQueryDto> orderItems = em.createQuery(
        "select new jpabook.jpashop.repository.order.query.OrderQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
            + "from OrderItem oi "
            + "join oi.item i "
            + "where oi.order.id in :orderIds ", OrderItemQueryDto.class)
        .setParameter("orderIds", orderIds).getResultList();

    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
        .collect(Collectors.groupingBy(OrderItemQueryDto::getOrderId));
    return orderItemMap;
  }

}
