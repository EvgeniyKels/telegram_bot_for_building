package kls.tgb.mapper;

import kls.tgb.dao.entities.StateEntity;
import kls.tgb.dto.StateDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class StateMapper {

    public abstract StateDto fromStateEntityToStateDto(StateEntity stateEntity);

}
