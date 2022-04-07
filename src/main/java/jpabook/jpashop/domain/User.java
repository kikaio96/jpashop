package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id @GeneratedValue
	@Column(name="user_id")
	private long id;

	private String name;
	private String number;
	private String phone;

	@Column(unique = true)
	private String email;

	private String password;
	private String birthday;

	@Enumerated(EnumType.STRING)
	private UserState state;

	public void changeUserByUser(String phone, String password, String birthday) {
		this.phone = phone;
		this.password = password;
		this.birthday = birthday;
	}

	public void changeUserByAdmin(String name, String number, String phone, String birthday) {
		this.name = name;
		this.number = number;
		this.phone = phone;
		this.birthday = birthday;
	}

	public void changeUserState(UserState state) {
		this.state = state;
	}
}
