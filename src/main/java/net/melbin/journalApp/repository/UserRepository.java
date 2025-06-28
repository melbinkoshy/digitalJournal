package net.melbin.journalApp.repository;

import net.melbin.journalApp.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;


public interface UserRepository extends MongoRepository<User, ObjectId> {
    User findByUsername(String username);
    void deleteByUsername(String username);
}
