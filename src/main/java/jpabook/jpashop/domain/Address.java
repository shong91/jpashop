package jpabook.jpashop.domain;

import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
public class Address {

  private String city;
  private String street;
  private String zipcode;

  protected Address() {
    // JPA spec 상 @Entity, @Embeddable 타입은 기본 생성자를 public or protected 로 설정하여야 한다.
    // 안전성을 위해 public 보다는 protected 를 사용하는 편
    // why? JPA 구현 라이브러리가 객체를 생성할 대 리플랙션 같은 기술을 사용할 수 있도록 지원해야 하기 때문
  }

  // 값 타임은 변경 불가하게 설계한다.
  // why? @Setter 를 열어두면 애플리케이션이 커질수록 어디서 무엇을 변경하였는지 추적하기 어려워짐
  public Address(String city, String street, String zipcode) {
    this.city = city;
    this.street = street;
    this.zipcode = zipcode;
  }
}
