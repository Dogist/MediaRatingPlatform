package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.dto.UserProfileDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.exceptions.NotFoundException;
import at.fhtw.mrp.exceptions.UnauthorizedException;

import java.util.List;
import java.util.Objects;

public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void createUser(UserAuthDTO user) throws DataConflictException {
        if (user == null) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        userDao.createUser(user.username(), HashUtil.generateHashedPassword(user.password()));
    }

    @Override
    public UserProfileDTO getUserProfile(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        UserEntity userEntity = userDao.getUserById(userId);

        if (userEntity == null) {
            throw new NotFoundException("Dieser User kann nicht gefunden werden.");
        }
        userDao.hydrateUserStatistics(userEntity);

        return new UserProfileDTO(userEntity);
    }

    @Override
    public void updateUserProfile(Long userId, UserProfileUpdateDTO userProfile) {
        ValidationUtil.validateEntityId(userId, "User");

        UserEntity userEntity = userDao.getUserById(userId);

        if (userProfile == null) {
            throw new InvalidInputException("Es müssen Benutzerdaten übergeben werden.");
        } else if (!Objects.equals(userEntity.getId(), userId)) {
            throw new UnauthorizedException("Der aktuelle Benutzer darf diesen User nicht ändern.");
        }
        boolean userUpdated = userDao.updateUser(userId, userProfile);
        if (!userUpdated) {
            throw new InvalidInputException("Dieser Benutzer existiert nicht.");
        }
    }

    @Override
    public List<UserEntity> getUsersByRatingCount() {
        List<UserEntity> usersByRatingCount = userDao.getUsersByRatingCount();
        usersByRatingCount.forEach(userDao::hydrateUserStatistics);

        return usersByRatingCount;
    }
}
