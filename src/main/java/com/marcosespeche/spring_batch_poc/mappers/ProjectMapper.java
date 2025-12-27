package com.marcosespeche.spring_batch_poc.mappers;

import com.marcosespeche.spring_batch_poc.domain.projects.dtos.ReadProjectDTO;
import com.marcosespeche.spring_batch_poc.entities.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ReadProjectDTO toReadProjectDTO (Project project);
}
