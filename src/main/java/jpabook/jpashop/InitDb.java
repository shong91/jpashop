package jpabook.jpashop;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Delivery;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 초기 데이터 적재용 initDB 클래스
 * <p>
 * user1 JPA1 book JPA2 book
 * <p>
 * user2 SPRING1 book SPRING2 book
 */
@Component
@RequiredArgsConstructor
public class InitDb {

  private final InitService initService;

  @PostConstruct
  public void init() {
    initService.dbInit1();
    initService.dbInit2();
  }

  @Component
  @RequiredArgsConstructor
  @Transactional
  static class InitService {

    private final EntityManager em;

    public void dbInit1() {
      Member member = createMember("user1", "seoul", "street", "11111");

      Book book1 = createBook("JPA1 book", 10000, 100);

      Book book2 = createBook("JPA2 book", 20000, 100);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());

      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }

    public void dbInit2() {
      Member member = createMember("user2", "busan", "street", "11111");

      Book book1 = createBook("SPRING2 book", 20000, 200);

      Book book2 = createBook("SPRING2 book", 40000, 400);

      OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 2);
      OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

      Delivery delivery = new Delivery();
      delivery.setAddress(member.getAddress());

      Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
      em.persist(order);
    }

    private Book createBook(String name, int price, int stockQuantity) {
      Book book1 = new Book();
      book1.setName(name);
      book1.setPrice(price);
      book1.setStockQuantity(stockQuantity);
      em.persist(book1);
      return book1;
    }

    private Member createMember(String name, String city, String street, String zipcode) {
      Member member = new Member();
      member.setName(name);
      member.setAddress(new Address(city, street, zipcode));
      em.persist(member);
      return member;
    }
  }
}


