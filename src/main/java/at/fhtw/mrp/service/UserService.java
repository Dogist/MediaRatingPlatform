package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dao.general.DataConflictException;
import at.fhtw.mrp.dto.UserAuth;
import at.fhtw.mrp.exceptions.InvalidInputException;
import org.apache.commons.lang3.StringUtils;

public class UserService {
    private final UserDao userDao = new UserDao();

    public void createUser(UserAuth user) throws DataConflictException {
        if (user == null || StringUtils.isBlank(user.password()) || StringUtils.isBlank(user.username())) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        // TODO Hash Password
        userDao.createUser(user.username(), user.password());
    }
}
