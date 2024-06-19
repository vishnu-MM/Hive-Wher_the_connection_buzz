package com.hive.chat_service.Service;

import com.hive.chat_service.DTO.GroupDTO;
import com.hive.chat_service.Entity.Group;
import com.hive.chat_service.Repository.GroupDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupDAO dao;

    public GroupDTO createGroup(GroupDTO groupDTO) {
        Group group = dtoToEntity(groupDTO);
        group = dao.save(group);
        groupDTO = entityToDTO(group);
        return groupDTO;
    }

    private GroupDTO entityToDTO(Group group) {
        return GroupDTO.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .membersId(group.getMembersId())
                .imageData(group.getImageData())
                .imageName(group.getImageName())
                .imageType(group.getImageType())
                .build();
    }

    private Group dtoToEntity(GroupDTO groupDTO) {
        return Group.builder()
                .id(groupDTO.getId())
                .groupName(groupDTO.getGroupName())
                .membersId(groupDTO.getMembersId())
                .imageData(groupDTO.getImageData())
                .imageName(groupDTO.getImageName())
                .imageType(groupDTO.getImageType())
                .build();
    }

    public List<GroupDTO> findAllGroupByUser(String userId) {
        return dao.findByMembersId(userId).stream().map(this::entityToDTO).toList();
    }
}
