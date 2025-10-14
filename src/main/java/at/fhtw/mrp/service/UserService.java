package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.dto.UserProfileDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.exceptions.NotFoundException;

public class UserService {
    private final UserDao userDao = new UserDao();

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

        if(userEntity == null) {
            throw new NotFoundException("Dieser User kann nicht gefunden werden.");
        }
        // TODO add Statistics
        return new UserProfileDTO(userEntity);
    }

    public void updateUserProfile(Long userId, UserProfileUpdateDTO userProfile) {
        ValidationUtil.validateEntityId(userId, "User");

        if (userProfile == null) {
            throw new InvalidInputException("Es müssen Benutzerdaten übergeben werden.");
        }
        // FIXME sollten Benutzer die Profile von anderen editieren?
        boolean userUpdated = userDao.updateUser(userId, userProfile);
        if (!userUpdated) {
            throw new InvalidInputException("Dieser Benutzer existiert nicht.");
        }
    }
}
