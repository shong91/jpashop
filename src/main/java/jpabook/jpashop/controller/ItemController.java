package jpabook.jpashop.controller;

import java.util.List;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ItemController {

  private final ItemService itemService;

  @GetMapping("/items/new")
  public String createForm(Model model) {
    model.addAttribute("form", new BookForm());
    return "items/createItemForm";
  }

  @PostMapping("/items/new")
  public String create(BookForm form) {

    // setter 를 모두 제거하고 생성자 함수를 사용하는 것이 더욱 깔끔한 설계.
    // 예제니까 간단하게 그냥 setter 를 사용했습니다 ...
    Book book = new Book();
    book.setName(form.getName());
    book.setPrice(form.getPrice());
    book.setStockQuantity(form.getStockQuantity());
    book.setAuthor(form.getAuthor());
    book.setIsbn(form.getIsbn());

    itemService.saveItem(book);
    return "redirect:/";
  }

  @GetMapping("/items")
  public String list(Model model) {
    List<Item> items = itemService.findItems();
    model.addAttribute("items", items);
    return "items/itemList";

  }

  @GetMapping("/items/{itemId}/edit")
  public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
    Book item = (Book) itemService.findOne(itemId);

    // Book Entity 가 아닌 Form 객체를 사용
    BookForm form = new BookForm();
    form.setId(item.getId());
    form.setPrice(item.getPrice());
    form.setStockQuantity(item.getStockQuantity());
    form.setAuthor(item.getAuthor());
    form.setIsbn(item.getIsbn());

    model.addAttribute("form", form);

    return "items/updateItemForm";
  }

  @PostMapping("/items/{itemId}/edit")
  public String updateItem(@PathVariable("itemId") Long itemId,
      @ModelAttribute("form") BookForm form) {

    // 업데이트 전, 로그인한 유저가 해당 객체에 대한 업데이트 권한이 있는지 체크하는 로직 필요.

    // 컨트롤러에서 어설프게 엔티티를 생성하지 말고, 서비스 계층에 명확하게 파라미터를 전달하자.
//    Book book = new Book();
//    book.setId(form.getId());
//    book.setName(form.getName());
//    book.setPrice(form.getPrice());
//    book.setStockQuantity(form.getStockQuantity());
//    book.setAuthor(form.getAuthor());
//    book.setIsbn(form.getIsbn());
//
//    itemService.saveItem(book);

    itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());
    return "redirect:items";
  }
}
