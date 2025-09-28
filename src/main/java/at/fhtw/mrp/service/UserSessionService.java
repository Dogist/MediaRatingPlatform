package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.entity.UserEntity;

public class UserSessionService {

    private static final UserDao userDao = new UserDao();

    private static final ThreadLocal<UserEntity> userSession = new ThreadLocal<>();

    public static void registerUserSession(String username) {
        UserEntity user = userDao.getUserByUsername(username);
        user.setPassword("");
        userSession.set(user);
    }

    public static UserEntity getUserSession() {
        return userSession.get();
    }

    public static void clearUserSession() {
        userSession.set(null);
        userSession.remove();
    }
}
