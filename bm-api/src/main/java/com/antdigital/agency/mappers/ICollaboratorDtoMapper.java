package com.antdigital.agency.mappers;

import com.antdigital.agency.dal.entity.Collaborator;
import com.antdigital.agency.dtos.response.CollaboratorDetailDto;
import com.antdigital.agency.dtos.response.CollaboratorDto;
import com.antdigital.agency.dtos.response.CollaboratorFullDto;
import com.antdigital.agency.dtos.response.CollaboratorTempDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ICollaboratorDtoMapper {
    ICollaboratorDtoMapper INSTANCE = Mappers.getMapper(ICollaboratorDtoMapper.class);
    @Mapping(source = "saleRankId", target = "saleRank.id")
    CollaboratorDto toCollaboratorDto (Collaborator collaborator);
    CollaboratorTempDto toCollaboratorTempDto (Collaborator collaborator);
    @Mapping(source = "saleRank.id", target = "saleRankId")
    Collaborator toCollaborator (CollaboratorDto collaboratorDto);
    @Mapping(source = "saleRankId", target = "saleRank.id")
    CollaboratorFullDto toCollaboratorFullDto (Collaborator collaborator);
    @Mapping(source = "saleRank.id", target = "saleRankId")
    Collaborator toCollaborator (CollaboratorFullDto collaboratorFullDto);
    CollaboratorDetailDto toCollaboratorDetailDto (CollaboratorDto collaboratorDto);
    List<CollaboratorDto> toCollaboratorDtoList(List<Collaborator> collaborators);
    List<CollaboratorTempDto> toCollaboratorTempDtoList(List<Collaborator> collaborators);
    List<CollaboratorDetailDto> toCollaboratorDetailDtoList(List<CollaboratorDto> collaboratorDtoList);
}
