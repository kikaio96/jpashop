package jpabook.jpashop.controller;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.service.BookService;
import jpabook.jpashop.service.LendService;
import jpabook.jpashop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final BookService bookService;
    private final LendService lendService;

    @GetMapping(path="/users")
    public String userList(@RequestParam(name="start", required=false, defaultValue="0") int start, ModelMap model) {
        int listCount = 5;
        List<User> userList = userService.getUserList(start, listCount);

        int count = userService.userCount();
        int pageCount = count/listCount;
        if(count % listCount > 0)
            pageCount++;

        List<Integer> pageStartList = new ArrayList<>();
        for(int i = 0; i < pageCount; i++) {
            pageStartList.add(i * listCount);
        }

        model.addAttribute("userList", userList);
        model.addAttribute("count", count);
        model.addAttribute("pageStartList", pageStartList);

        return "admin/userlist";
    }

    @GetMapping(path="/user")
    public String userRegist(ModelMap model) {
        model.addAttribute("user", new User());
        return "admin/userregist";
    }

    @GetMapping(path="/users/{userId}")
    public String userInfo(@PathVariable Long userId, ModelMap model, RedirectAttributes redirectAttr) {
        User user;
        try {
            user = userService.getUser(userId);
            model.addAttribute("userInfo", user);
        } catch(Exception e) {
            redirectAttr.addFlashAttribute("errorCode", 500);
            redirectAttr.addFlashAttribute("errorMessage", "알 수 없는 사용자입니다.");
            return "redirect:/admin/users";
        }

        return "admin/userinfo";
    }

    @PostMapping(path="/user")
    public String userRegist(@ModelAttribute User user, RedirectAttributes redirectAttr) {
        user.setPassword("1111");
        try {
            user.changeUserState(UserState.ACTIVATE);
            userService.addUser(user);
        } catch(DataIntegrityViolationException e) {
            redirectAttr.addFlashAttribute("errorCode", 500);
            redirectAttr.addFlashAttribute("errorMessage", "이미 존재하는 아이디입니다.");
            return "redirect:/admin/user";
        }
        return "redirect:/admin/users";
    }

    @PutMapping(path="/users/{user_id}")
    public String userModify(@ModelAttribute User user) {
        userService.updateUserByAdmin(user);

        return "redirect:/admin/users/" + user.getId();
    }

    @PatchMapping(path="/users/{userId}")
    public String changeState(@PathVariable Long userId, @RequestParam(name="state", required=true) UserState userState) {
        userService.changeState(userId, userState);
        return "redirect:/admin/users";
    }

    @GetMapping(path="/books")
    public String books(@RequestParam(name="start", required=false, defaultValue="0") int start, ModelMap model) {
        int listCount = 5;
        // 책 목록 select
        List<Book> books = bookService.getBookList(start, listCount);

        // 전체 페이지수 구하기
        int count = bookService.countBook();
        int pageCount = count / listCount;
        if(count % listCount > 0)
            pageCount++;

        List<Integer> pageStartList = new ArrayList<>();
        for(int i = 0; i < pageCount; i++) {
            pageStartList.add(i * listCount);
        }

        model.addAttribute("books", books);
        model.addAttribute("count", count);
        model.addAttribute("pageStartList", pageStartList);

        return "admin/booklist";
    }

    @GetMapping(path = "/books/{bookId}")
    public String bookInfo(@PathVariable Long bookId, ModelMap model) {
        Long book_id = bookId;
        Book book = bookService.getBook(book_id);

        model.addAttribute("book", book);

        return "admin/bookinfo";
    }

    @GetMapping(path="/book")
    public String bookRegist(ModelMap model) {
        model.addAttribute("book", new Book());
        return "admin/bookregist";
    }

    @PutMapping(path = "/books/{bookId}")
    public String bookModify(@ModelAttribute Book book) {
        bookService.updateBook(book);
        return "redirect:/admin/books/" + book.getId();
    }

    @PostMapping(path = "/book")
    public String regist(Book book, RedirectAttributes redirectAttr) {
        int remain = book.getTotal_count();
        book.changeBookHold(book.getTotal_count(), remain);
        book.changeBookState(BookState.ACTIVATE);

        try {
            bookService.addBook(book);
            return "redirect:/admin/books/";
        } catch (DataIntegrityViolationException e) {
            redirectAttr.addFlashAttribute("errorCode", 500);
            redirectAttr.addFlashAttribute("errorMessage", "이미 존재하는 도서입니다.");
            return "redirect:/admin/book";
        }
    }

    @PatchMapping(path = "/books/{bookId}")
    public String changeState(@PathVariable Long bookId, @RequestParam(name="state", required=true) BookState bookState) {
        bookService.changeState(bookId, bookState);
        return "redirect:/admin/books";
    }

    @GetMapping(path="/lends")
    public String lendList(@RequestParam(name="start", required=false, defaultValue="0") int start, HttpSession session, ModelMap model) {
        int listCount = 5;

        List<Lend> lendList = lendService.getLendList(start, listCount);

        // 전체 페이지수 구하기
        int count = lendService.lendCount();
        int pageCount = count / listCount;
        if(count % listCount > 0)
            pageCount++;

        List<Integer> pageStartList = new ArrayList<>();
        for(int i = 0; i < pageCount; i++) {
            pageStartList.add(i * listCount);
        }

        model.addAttribute("lendList", lendList);
        model.addAttribute("count", count);
        model.addAttribute("pageStartList", pageStartList);

        return "admin/lendlist";
    }
}
