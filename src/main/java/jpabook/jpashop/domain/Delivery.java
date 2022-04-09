package jpabook.jpashop.domain;


import static javax.persistence.FetchType.LAZY;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

  @Id
  @GeneratedValue
  @Column(name = "delivery_id")
  private Long id;

  @OneToOne(mappedBy = "delivery", fetch = LAZY)
  private Order order;

  @Embedded
  private Address address;

  @Enumerated(EnumType.STRING)
  // enum 을 사용 시에는 @Enumerated 을 사용하여야 하는데, 반드시 *String* type 으로 설정하여야 한다.
  // default= ORDINAL 으로 설정 시 0,1,2 등의 숫자로 입력되게 되는데, 중간에 Status 가 추가/변경될 시 값이 꼬이며 critical issue 를 발생시킬 확률이 높음
  private DeliveryStatus status;

}
