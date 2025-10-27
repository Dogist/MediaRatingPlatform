package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.dto.UserProfileDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;

import java.util.List;

public interface UserService {
    void createUser(UserAuthDTO user) throws DataConflictException;

    UserProfileDTO getUserProfile(Long userId);

    void updateUserProfile(Long userId, UserProfileUpdateDTO userProfile);

    List<UserEntity> getUsersByRatingCount();
}
