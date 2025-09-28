package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.dto.UserProfileDTO;
import at.fhtw.mrp.dto.UserProfileUpdateDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;
import org.apache.commons.lang3.StringUtils;

public class UserService {
    private final UserDao userDao = new UserDao();

    public void createUser(UserAuthDTO user) throws DataConflictException {
        if (user == null || StringUtils.isBlank(user.password()) || StringUtils.isBlank(user.username())) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        // TODO Hash Password
        userDao.createUser(user.username(), user.password());
    }

    public UserProfileDTO getUserProfile(Long userId) {
        if (userId < 1) {
            throw new InvalidInputException("Diese User-ID ist ungültig.");
        }

        UserEntity userEntity = userDao.getUserById(userId);
        // TODO add Statistics
        return new UserProfileDTO(userEntity);
    }

    public void updateUserProfile(Long userId, UserProfileUpdateDTO userProfile) {
        if (userId < 1) {
            throw new InvalidInputException("Diese User-ID ist ungültig.");
        }
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
