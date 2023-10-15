package com.shopmax.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopmax.constant.ItemSellStatus;
import com.shopmax.dto.ItemSearchDto;
import com.shopmax.dto.MainItemDto;
import com.shopmax.dto.QMainItemDto;
import com.shopmax.entity.Item;
import com.shopmax.entity.QItem;
import com.shopmax.entity.QItemImg;

import jakarta.persistence.EntityManager;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

	private JPAQueryFactory queryFactory;
	
	public ItemRepositoryCustomImpl(EntityManager em) {
		this.queryFactory = new JPAQueryFactory(em);
	}

	// 현재 날짜로부터 이전날짜를 구해주는 메소드
	private BooleanExpression regDtsAfter(String searchDateType) {
		
		LocalDateTime dateTime = LocalDateTime.now(); // 현재 날짜, 시간

		if (StringUtils.equals("all", searchDateType) || searchDateType == null)
			return null;
		else if (StringUtils.equals("1d", searchDateType))
			dateTime = dateTime.minusDays(1); // 현재 날짜로부터 1일 전
		else if (StringUtils.equals("1w", searchDateType))
			dateTime = dateTime.minusWeeks(1); // 현재 날짜로부터 1주일 전
		else if (StringUtils.equals("1m", searchDateType))
			dateTime = dateTime.minusMonths(1); // 현재 날짜로부터 1달 전
		else if (StringUtils.equals("6m", searchDateType))
			dateTime = dateTime.minusMonths(6); // 현재 날짜로부터 6개월 전

		return QItem.item.regTime.after(dateTime); // Q객체 리턴
		
	}

	// 상태를 전체로 했을 떄 null이 들어있음으로 처리 한번 해줌
	private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
		
		return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
		
	}
	
	private BooleanExpression searchByLike(String searchBy, String searchQuery) {
		
		if(StringUtils.equals("itemNm", searchBy)) {
			// 등록자로 검색 시 
			return QItem.item.itemNm.like("%"+ searchQuery + "%"); // item_nm like %검색어%
		}
		else if (StringUtils.equals("createBy", searchBy)) {
			return QItem.item.createBy.like("%"+ searchQuery + "%"); // create_by like %검색어%
		}
		
		return null;
	}
	
	@Override
	public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

		/*
		 * select * from item where reg_time = ? and item_sell_status = ? and
		 * item_nm(create_by) like %검색어% order by item_id desc;
		 */
//날짜검색
		List<Item> content = queryFactory.selectFrom(QItem.item)
				.where(regDtsAfter(itemSearchDto.getSearchDateType()), 
						searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
						searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
				.orderBy(QItem.item.id.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();
		
		/*
		 * select count item where reg_time = ? and item_sell_status = ? and
		 * item_nm(create_by) like %검색어% order by item_id desc;
		 */
		
		long total = queryFactory.select(Wildcard.count).from(QItem.item)
					.where(regDtsAfter(itemSearchDto.getSearchDateType()), 
							searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
							searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
					.fetchOne();
		
		return new PageImpl<>(content, pageable, total);
	}

	private BooleanExpression itemNmLike(String searchQuery) {
		return StringUtils.isEmpty(searchQuery)? 
				null : QItem.item.itemNm.like("%" + searchQuery + "%"); 
	}

	@Override
	public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
		/*select item.id, item.itemNm, item.itemDetail, item_img.itemUrl,item.price 
		 * from item, item_img 
		 * where item img.item_id = item_img.item_id
		 * and item_img.repimg_yn = "Y"
		 * and item.item_nm like '%검색어%'
		 * order by item.item_id desc;
		 * */
		
		
		QItem item = QItem.item;
		QItemImg itemImg = QItemImg.itemImg;
		
		//dto객체로 바로 받아올 때는 
		//1. 컬럼과 dto객체의 필드가 일치해야 한다. 
		//2. dto객체의 생성자에 @쿼리프로젝션 을 반드시 사용해야 한다. 

		List<MainItemDto> content = queryFactory
				.select(
						new QMainItemDto(
								item.id,
								item.itemNm,
								item.itemDetail,
								itemImg.imgUrl,
								item.price)
						)
						.from(itemImg)
						.join(itemImg.item, item)
						.where(itemImg.repimgYn.eq("Y"))
						.where(itemNmLike(itemSearchDto.getSearchQuery()))
						.orderBy(item.id.desc())
						.offset(pageable.getOffset())
						.limit(pageable.getPageSize())
						.fetch();
		
		long total = queryFactory
				.select(Wildcard.count)
				.from(itemImg)
				.join(itemImg.item, item)
				.where(itemImg.repimgYn.eq("Y"))
				.where(itemNmLike(itemSearchDto.getSearchQuery()))
				.fetchOne();
		
		return new PageImpl<>(content,pageable,total);
		
	}
	
	

}