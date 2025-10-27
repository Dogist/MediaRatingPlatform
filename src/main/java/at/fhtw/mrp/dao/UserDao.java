package at.fhtw.mrp.dao;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;

import java.util.List;

public interface UserDao {
    void createUser(String username, String passwordHash) throws DataConflictException;

    UserEntity getUserById(Long userId);

    void hydrateUserStatistics(UserEntity user);

    UserEntity getUserByUsername(String username);

    List<UserEntity> getUsersByFavoriteMedia(long mediaEntryId);

    List<UserEntity> getUsersByLikedRating(long ratingId);

    boolean updateUser(Long userId, UserProfileUpdateDTO userEntity);

    List<UserEntity> getUsersByRatingCount();
}
