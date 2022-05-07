package jpabook.jpashop.service;

import java.util.List;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
// 단순 위임만 하는 역할의 서비스 - 이런 경우엔 Controller -> Repository 로 바로 접근하는것도 ok
public class ItemService {

  private final ItemRepository itemRepository;

  @Transactional
  public void saveItem(Item item) {
    itemRepository.save(item);
  }

  @Transactional
  public Item updateItem(Long itemId, String name, int price, int stockQuantity) {
    // findItem 은 영속성 컨텍스트에서 가져온 객체이므로, 값이 변경되면 변경 감지를 통해 자동 update
    Item findItem = itemRepository.findOne(itemId);
    findItem.change(name, price, stockQuantity);

    return findItem;
  }

  public List<Item> findItems() {
    return itemRepository.findAll();
  }

  public Item findOne(Long itemId) {
    return itemRepository.findOne(itemId);
  }
}
