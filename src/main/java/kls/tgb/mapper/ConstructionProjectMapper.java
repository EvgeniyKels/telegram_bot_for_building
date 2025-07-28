package kls.tgb.mapper;

import kls.tgb.dao.entities.ConstructionProjectEntity;
import kls.tgb.dto.ConstructionProjectDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class ConstructionProjectMapper {

    public abstract ConstructionProjectDto fromConstructionProjectEntityToConstructionProjectDto
            (ConstructionProjectEntity entity);

}
