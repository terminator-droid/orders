package com.dudev.orderservice.mapper;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.model.Order;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toEntity(CreateOrderDto order);


}
