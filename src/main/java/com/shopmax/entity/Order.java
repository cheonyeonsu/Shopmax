package com.shopmax.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.shopmax.constant.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="orders")
@Setter
@Getter
@ToString
public class Order {

	@Id
	@Column(name="order_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private LocalDateTime orderDate; //주문날짜. 
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus; //주문상태
	
	@ManyToOne
	@JoinColumn(name="member_id") 
	private Member member;
	
	//order에서도 orderitem을 참조할 수 있도록 양방향 관계를 만든다.
	//다만 orderitem은 자식 테이블이 되므로 List로 만든다. 
	@OneToMany(mappedBy = "order",  // 연관관계의 주인설정(외래키 지정.)
			cascade = CascadeType.ALL, // 연속성 정의. 부모자식 삭제, 수정 같이감.
			orphanRemoval = true, fetch = FetchType.LAZY) // 고아객체(부모랑 관계성이 끊어진 entitiy)를 제거한다. 
	private List<OrderItem> orderItems = new ArrayList<>();
	
	
	public void addOrderItem(OrderItem orderItem) {
		this.orderItems.add(orderItem);
		orderItem.setOrder(this); //★양방향 참조관계 일때는 orderItem객체에도 order객체를 세팅한다. 
	}
	
	//order 객체를 생성해준다. 
	public static Order createOrder(Member member, List<OrderItem> orderItemList) {
		Order order = new Order();
		order.setMember(member);
		
		for(OrderItem orderItem : orderItemList) {
			order.addOrderItem(orderItem);
		}
		
		order.setOrderStatus(OrderStatus.ORDER);
		order.setOrderDate(LocalDateTime.now());
		
		return order;
	}
	
	//총 주문금액
	public int getTotalPrice() {
		int totalPrice = 0;
		for(OrderItem orderItem : orderItems) {
			totalPrice += orderItem.getTotalPrice();
		}
		return totalPrice;
	}
	
	//주문취소
	public void cancelOrder() {
		this.orderStatus = OrderStatus.CANCEL;
		
		//재고를 원래대로 돌려놓는다.(재고는 오터아이템엔티티에.)
		for(OrderItem orderItem : orderItems) {
			orderItem.cancel();
		}
	}
}
