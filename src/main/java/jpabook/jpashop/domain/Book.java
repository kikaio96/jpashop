package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Book {

	@Id @GeneratedValue
	@Column(name="book_id")
	private Long id;

	private String name;

	@Column(columnDefinition = "LONGTEXT")
	private String info;

	@Column(unique = true)
	private String isbn;

	private String genre;
	private String author;
	private String plait;
	private String publisher;

	private int total_count;
	private int remain;

	@Enumerated(EnumType.STRING)
	private BookState state;

	public void chaneBook(String name, String info, String isbn, String genre, String author, String plait, String publisher) {
		this.name = name;
		this.info = info;
		this.isbn = isbn;
		this.genre = genre;
		this.author = author;
		this.plait = plait;
		this.publisher = publisher;
	}

	public void changeBookHold(int total_count, int remain) {
		this.total_count = total_count;
		this.remain = remain;
	}

	public void changeBookState(BookState state) {
		this.state = state;
	}
}
