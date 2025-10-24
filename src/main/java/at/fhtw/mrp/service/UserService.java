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

import java.util.Objects;

public class UserService {
    private final UserDao userDao = CDI.INSTANCE.getService(UserDao.class);

    public void createUser(UserAuthDTO user) throws DataConflictException {
        if (user == null) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        // TODO Hash Password
        userDao.createUser(user.username(), user.password());
    }

    public UserProfileDTO getUserProfile(Long userId) {
        ValidationUtil.validateEntityId(userId, "User");

        UserEntity userEntity = userDao.getUserById(userId);

        if (userEntity == null) {
            throw new NotFoundException("Dieser User kann nicht gefunden werden.");
        }
        userDao.hydrateUserStatistics(userEntity);

        return new UserProfileDTO(userEntity);
    }

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
}
