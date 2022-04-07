package jpabook.jpashop.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Lend;
import jpabook.jpashop.domain.LendState;
import jpabook.jpashop.domain.User;
import jpabook.jpashop.service.BookService;
import jpabook.jpashop.service.LendService;
import jpabook.jpashop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final BookService bookService;
	private final LendService lendService;
	
	@GetMapping(path="/login")
	public String login(ModelMap model) {
		return "user/fragments/login";
	}
	
	@PostMapping(path="/login")
	public String login(@RequestParam(name="email") String email, @RequestParam(name="password") String password, 
						HttpSession session, RedirectAttributes redirectAttr) {
		try {
			User user = userService.login(email, password);
			session.setAttribute("userInfo", user);
			return "redirect:/user/books";
		} catch (Exception e) {
			redirectAttr.addFlashAttribute("errorResult", -1);
			return "redirect:/user/login";
		}
	}
	
	@PostMapping(path="/logout")
	public String logout(HttpSession session) {
		session.removeAttribute("userInfo");
		return "redirect:/user/login";
	}
	
	@GetMapping(path="/userinfo")
	public String userInfo(HttpSession session, ModelMap model) {
		User user = (User)session.getAttribute("userInfo");
		
		try {
			model.addAttribute("userInfo", user);
		} catch(Exception e) {
			return "user/fragments/login";
		}
		
		return "user/userinfo";
	}
	
	@PutMapping(path="/users/{userId}")
	public String userModify(@ModelAttribute User user, @RequestParam(name="password_confirm") String pwdConfirm, 
							HttpSession session, RedirectAttributes redirectAttr) {
		User userInfo = (User)session.getAttribute("userInfo");

		try {
			userInfo.setPassword(user.getPassword());
			userInfo.setPhone(user.getPhone());
			userInfo.setBirthday(user.getBirthday());
		} catch(Exception e) {
			return "user/fragments/login";
		}
		
		if(userInfo.getPassword() == "") {
			redirectAttr.addFlashAttribute("errorResult", -2);
			return "redirect:/user/userinfo";
		}
		
		if(!user.getPassword().equals(pwdConfirm)) {
			redirectAttr.addFlashAttribute("errorResult", -1);
			return "redirect:/user/userinfo";
		}
		
		userService.updateUserByUser(userInfo);
		session.setAttribute("userInfo", userInfo);
		return "redirect:/user/userinfo";
	}
	
	@GetMapping(path="/books")
	public String bookList(@RequestParam(name="start", required=false, defaultValue="0") int start, ModelMap model) {
		int listCount = 5;
		// 책 목록 select
		List<Book> bookList = bookService.getBookListByUser(start, listCount);
		
		// 전체 페이지수 구하기
		int count = bookService.countBookByUser();
		int pageCount = count / listCount;
		if(count % listCount > 0)
			pageCount++;
		
		List<Integer> pageStartList = new ArrayList<>();
		for(int i = 0; i < pageCount; i++) {
			pageStartList.add(i * listCount);
		}
		
		model.addAttribute("bookList", bookList);
		model.addAttribute("count", count);
		model.addAttribute("pageStartList", pageStartList);
		
		return "user/booklist";
	}
	
	@GetMapping(path = "/books/{bookId}")
	public String bookInfo(@PathVariable Long bookId, ModelMap model) {
		Book book = bookService.getBook(bookId);
		
		model.addAttribute("book", book);
		
		return "user/bookinfo";
	}
	
	@PostMapping(path="/lend")
	public String bookLend(@RequestParam(name="book_id") Long bookId, HttpSession session, RedirectAttributes redirectAttr) {
		User user = (User)session.getAttribute("userInfo");
		Book book = bookService.getBook(bookId);
		
		// userInfo가 비어있는 경우 null exception
		// 로그인 화면으로 redirect
		long user_id;
		try {
			user_id = user.getId();
		} catch(Exception e) {
			return "redirect:/user/login";
		}
		
		Long lendRange = 14L;
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime returnDate = LocalDateTime.now().plusDays(lendRange);
		
		Lend lend = Lend.builder()
						.user(user)
						.book(book)
						.lendDate(now)
						.returnDate(returnDate)
						.build();
		
		// 대여 전에 도서 수량이 부족해진 경우
		try {
			lendService.save(lend);
		} catch (Exception e) {
			redirectAttr.addFlashAttribute("errorCode", 500);
			redirectAttr.addFlashAttribute("errorMessage", "도서 수량이 부족합니다.");
			return "redirect:/user/books/" + bookId;
		}
		
		return "redirect:/user/lends";
	}
	
	@GetMapping(path="/lends")
	public String lendList(@RequestParam(name="start", required=false, defaultValue="0") int start, HttpSession session, ModelMap model) {
		User user = (User)session.getAttribute("userInfo");
		
		long user_id;
		try {
			user_id = user.getId();
		} catch(Exception e) {
			return "user/fragments/login";
		}
		
		int listCount = 5;

		List<Lend> lendList = lendService.getLendList(start, listCount, user_id);
		
		// 전체 페이지수 구하기
		int count = lendService.lendCount(user_id);
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
		
		return "user/lendlist";
	}
	
	@DeleteMapping(path="/return")
	public String bookReturn(@RequestParam(name="lend_id") Long lendId) {
		lendService.returnLend(lendId, LendState.RETURN);
		return "redirect:/user/lends";
	}
}
