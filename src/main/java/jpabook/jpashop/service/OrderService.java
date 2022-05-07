package jpabook.jpashop.service;

import java.util.List;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final MemberRepository memberRepository;
  private final ItemRepository itemRepository;

  /**
   * 주문
   */
  @Transactional
  public Long order(Long memberId, Long itemId, int count) {
    // 엔티티 조회
    Member member = memberRepository.findOne(memberId);
    Item item = itemRepository.findOne(itemId);

    // 배송정보 생성
    Delivery delivery = new Delivery();
    delivery.setAddress(member.getAddress());

    // 주문 상품 생성
    OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

    // 주문 생성
    Order order = Order.createOrder(member, delivery, orderItem);

    // 주문 저장
    // Order entity 의 cascade 설정 => delivery, orderitem, order 의 영속성을 한번에 persist
    orderRepository.save(order);
    return order.getId();
  }

  /**
   * 주문 취소
   */
  @Transactional
  public void cancelOrder(Long orderId) {
    // 주문 엔티티 조회
    Order order = orderRepository.findOne(orderId);

    // 주문 취소
    // 엔티티가 변경되면, JPA 가 자동으로 변경 내역 감지(dirty checking) 하여 업데이트 쿼리 실행
    order.cancel();
  }

  /**
   * 검색
   */
  public List<Order> findOrders(OrderSearch orderSearch) {
    return orderRepository.findAllByString(orderSearch);
  }
}
