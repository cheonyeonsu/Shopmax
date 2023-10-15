package com.shopmax.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="cart")
@Setter
@Getter
@ToString
public class Cart {
	
	@Id
	@Column(name="cart_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;	
	
	//맵핑관계 적용(1:1관계성.)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id") //프라이머리키로 사용.
	private Member member;
}
