package com.hive.chat_service.Repository;

import com.hive.chat_service.Entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDAO extends MongoRepository<Group, String> {
    List<Group> findByMembersId(String memberId);
}
