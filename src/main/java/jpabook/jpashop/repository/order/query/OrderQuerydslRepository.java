package jpabook.jpashop.repository.order.query;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QDelivery;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import jpabook.jpashop.domain.QOrderItem;
import jpabook.jpashop.repository.OrderSearch;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public class OrderQuerydslRepository {

  private final EntityManager em;
  private final JPAQueryFactory query;

  private QOrder order = QOrder.order;
  private QMember member = QMember.member;
  private QDelivery delivery = QDelivery.delivery;
  private QOrderItem orderItem = QOrderItem.orderItem;

  public OrderQuerydslRepository(EntityManager em) {
    this.em = em;
    this.query = new JPAQueryFactory(em);
  }

  public List<Order> findAll(OrderSearch orderSearch) {
    // 3. Querydsl (권장)
    return query
        .select(order)
        .from(order)
        .join(order.member, member)
        .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
        .limit(1000)
        .fetch();
  }

  public List<Order> findAllWithMemberDelivery(int offset, int limit) {
    return query
        .select(order)
        .from(order)
        .join(order.member, member)
        .join(order.delivery, delivery)
        .offset(offset)
        .limit(limit)
        .fetch();
  }

  public List<Order> findAllWithItem() {
    return query
        .selectDistinct(order)
        .from(order)
        .join(order.member, member)
        .join(order.delivery, delivery)
        .join(order.orderItems, orderItem)
        .fetch();
  }


  private BooleanExpression nameLike(String memberName) {
    if (!StringUtils.hasText(memberName)) {
      return null;
    }
    return QMember.member.name.like(memberName);
  }

  private BooleanExpression statusEq(OrderStatus statusCond) {
    if (statusCond == null) {
      return null;
    }
    return order.status.eq(statusCond);
  }

}
