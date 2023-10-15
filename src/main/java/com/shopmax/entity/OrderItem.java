package com.shopmax.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="order_item")
@Setter
@Getter
@ToString
public class OrderItem extends BaseEntity {
	
	@Id
	@Column(name="order_item_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne(fetch = FetchType.LAZY)//다 안가져오고 필요한 테이블만 조인해서 가져옴.
	@JoinColumn(name = "order_id")
	private Order order;
	
	private int orderPrice; //주문가격
	
	private int count; //수량
	
	//주문할 상품하고 주문 수량을 통해 orderitem객체를 만드는 기능. 
	public static OrderItem createOrderItem(Item item, int count) {
		OrderItem orderitem = new OrderItem();
		orderitem.setItem(item);
		orderitem.setCount(count);
		orderitem.setOrderPrice(item.getPrice());
		
		item.removeStock(count); //재고감소
		
		return orderitem;
	}
	
	public int getTotalPrice() {
		return orderPrice * count;
	}
	
	public void cancel() {
		this.getItem().addStock(count);
	}
}
