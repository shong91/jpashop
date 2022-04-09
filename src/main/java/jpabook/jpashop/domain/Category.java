package jpabook.jpashop.domain;

import static javax.persistence.FetchType.LAZY;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Category {

  @Id
  @GeneratedValue
  @Column(name = "category_id")
  private Long id;

  private String name;

  @ManyToMany
  @JoinTable(name = "category_item",
      joinColumns = @JoinColumn(name = "category_id"),
      inverseJoinColumns = @JoinColumn(name = "item_id"))
  private List<Item> items = new ArrayList<>();
  // @ManyToMany 는 실무에 적용하기에는 한계가 있음 !!
  // why? 중간 테이블에 컬럼을 추가할 수 없고(joinColumn 만 가능하며, 공통컬럼 등 기본 컬럼 세팅 불가), 세밀하게 쿼링하기 어려움.
  // => 실무에서는 다대다 매핑(@ManyToMany) 을 그대로 사용하지 말고,
  // 중간 엔티티를 만들고 일대다-다대일 매핑(@ManyToOne - @OneToMany)으로 풀어내어 사용하자.

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @OneToMany(mappedBy = "parent")
  private List<Category> child = new ArrayList<>();

  // 연관관계 메서드
  public void addChildCategory(Category child) {
    this.child.add(child);
    child.setParent(this);
  }
}
