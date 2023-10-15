package com.shopmax.dto;

import com.shopmax.entity.OrderItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {
	
	//앤티티 > Dto로 바꿔준다. 
	public OrderItemDto(OrderItem orderItem, String string) {
		this.itemNm = orderItem.getItem().getItemNm();
		this.count = orderItem.getCount();
		this.orderPrice = orderItem.getOrderPrice();
		this.imgUrl = string;
	}
	
	private String itemNm; //상품명
	
	private int count; //주문수량
	
	private int orderPrice; //주문 금액
	
	private String imgUrl; //상품 이미지 경로. 
}
