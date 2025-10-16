package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.*;

import java.util.HashMap;
import java.util.Map;

public enum CDI {
    INSTANCE;

    private final Map<Class<?>, Object> services;

    CDI() {
        services = new HashMap<>();
        UserDao userDao = new UserDaoImpl();
        RatingDao ratingDao = new RatingDaoImpl(userDao);
        MediaEntryDao mediaEntryDao = new MediaEntryDaoImpl(userDao, ratingDao);

        services.put(AuthService.class, new BearerAuthServiceImpl(userDao));
        services.put(UserDao.class, userDao);
        services.put(MediaEntryDao.class, mediaEntryDao);
        services.put(RatingDao.class, ratingDao);
    }

    public <T> T getService(Class<T> serviceClass) {
        Object o = services.get(serviceClass);
        if (o != null) {
            return (T) o;
        }
        throw new RuntimeException("Kein Service für die Klasse " + serviceClass.getName() + " gefunden.");
    }
}
