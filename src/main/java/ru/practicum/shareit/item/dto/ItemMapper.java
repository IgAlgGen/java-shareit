package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.model.Item;

/**
 * Маппер для Item.
 */

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {
    ItemDto toDto(Item item);

    @Mapping(target = "ownerId", ignore = true)
    Item toItem(ItemDto itemDto);
}