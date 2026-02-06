package com.dudev.orderservice.mapper;

import com.dudev.orderservice.dto.CreateOrderDto;
import com.dudev.orderservice.dto.OrderDto;
import com.dudev.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderDto toDto(Order order);

    Order toEntity(CreateOrderDto order);


}
