package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);
        List<OrderHistDto> orderHistDtos = new ArrayList<>(); //주문 히스토리 리스트 객체 생성
        //Order -> OrderHistDto
        //OrderItem -> OrderItemDto

        for(Order order : orders){// 주문리스트 -> 주문
            //주문히스토리 객체 생성시 매개변서 -> 주문 // 객체생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            //주문에 있는 주문 아이템 리스트를 추출
            List<OrderItem> orderItems = order.getOrderItems();
            //주문 아이템 리스트에서 -> 주문 아이템
            for(OrderItem orderItem : orderItems){
                //주문아이템에 있는 item을 추출하고 item id를 매개변수로 입력
                //itemImg 객체를 추출 -> 조건 "Y" 대표이미지 ItemImg
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(),"Y");
               //OrderItemDto 객체를 생성 -> 매개변수 orderItem객체, itemImg -> url
                OrderItemDto orderItemDto =new OrderItemDto(orderItem, itemImg.getImgUrl());
                //주문히스토리 객체에 OrderItemDto를 추가
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            //주문 히스토리 리스트에 주문히스토리 객체를 추가합니다.
            orderHistDtos.add(orderHistDto);
        }
        //Page자료구조에 제네릭 OrderHistDto리턴
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){ //1,a@naver.com
        Member curMember = memberRepository.findByEmail(email); //현재 Member 객체 추출
        //orderId를 매개변수로 넣어서 주문서 추출
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        //주문서에 있는 이메일 주소를 savedMember에 대입
        Member savedMember = order.getMember();
        //현재 로그인 된 이메일 주문서에 있는 이메일 비교하는거 같으면 true 다르면 false
        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false; //다르면 실행
        }
        return true; //같으면 실행
    }
    public void cancelOrder(Long orderId){
        //orderId를 통한 주문서 추출
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        //Entity Order cancelOrder 메소드 추출
        order.cancelOrder();
    }

}
