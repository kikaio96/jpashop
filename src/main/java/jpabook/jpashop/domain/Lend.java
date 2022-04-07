package jpabook.jpashop.domain;

import java.time.LocalDateTime;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lend {

	@Id @GeneratedValue
	@Column(name="lend_id")
	private long id;

	@Column(name="lend_date")
	private LocalDateTime lendDate;
	@Column(name="return_date")
	private LocalDateTime returnDate;

	@ManyToOne
	@JoinColumn(name="user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name="book_id")
	private Book book;

	@Enumerated(EnumType.STRING)
	private LendState state;

	public void chaneReturnDate(LocalDateTime returnDate) {
		this.returnDate = returnDate;
	}

	public void changeLendState(LendState state) {
		this.state = state;
	}
}
