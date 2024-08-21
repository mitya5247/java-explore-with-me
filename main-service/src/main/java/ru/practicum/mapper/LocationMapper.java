package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.practicum.model.location.Location;
import ru.practicum.model.location.dto.LocationDto;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LocationMapper {

    LocationDto locationToDto(Location location);

    Location dtoTolocation(LocationDto locationDto);


}
