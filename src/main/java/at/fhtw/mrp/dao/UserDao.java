package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;

import java.util.List;

public interface UserDao {
    void createUser(String username, String passwordHash) throws DataConflictException;

    boolean checkUserAuth(String username, String passwordHash);

    UserEntity getUserById(Long userId);

    UserEntity getUserByUsername(String username);

    List<UserEntity> getUserByFavoriteMedia(long mediaEntryId);

    List<UserEntity> getUserByLikedRating(long ratingId);

    boolean updateUser(Long userId, UserProfileUpdateDTO userEntity);
}
