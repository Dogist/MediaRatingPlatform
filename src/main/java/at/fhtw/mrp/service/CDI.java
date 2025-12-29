package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.*;
import at.fhtw.mrp.dao.general.DatabaseManager;
import at.fhtw.mrp.dao.general.DatabaseManagerImpl;

import java.util.HashMap;
import java.util.Map;

public enum CDI {
    INSTANCE;

    private final Map<Class<?>, Object> services;

    CDI() {
        services = new HashMap<>();
        DatabaseManagerImpl databaseManager = new DatabaseManagerImpl();
        UserDao userDao = new UserDaoImpl(databaseManager);
        RatingDao ratingDao = new RatingDaoImpl(databaseManager, userDao);
        MediaEntryDao mediaEntryDao = new MediaEntryDaoImpl(databaseManager, userDao, ratingDao);

        services.put(DatabaseManager.class, databaseManager);
        services.put(AuthService.class, new BearerAuthServiceImpl(userDao));
        services.put(UserDao.class, userDao);
        services.put(MediaEntryDao.class, mediaEntryDao);
        services.put(RatingDao.class, ratingDao);
        services.put(UserService.class, new UserServiceImpl(userDao));
        services.put(MediaService.class, new MediaServiceImpl(mediaEntryDao));
        services.put(RatingService.class, new RatingServiceImpl(mediaEntryDao, ratingDao));
    }

    public <T> T getService(Class<T> serviceClass) {
        Object o = services.get(serviceClass);
        if (o != null) {
            return (T) o;
        }
        throw new RuntimeException("Kein Service für die Klasse " + serviceClass.getName() + " gefunden.");
    }

    /**
     * <b>Diese Funktion ist nur eine Test-Funktion! nicht im normalen Code verwenden!</b>
     */
    public <T> void overrideService(Class<T> serviceClass, T service) {
        Object o = services.get(serviceClass);
        if (o != null) {
            services.put(serviceClass, service);
        } else {
            throw new RuntimeException("Kein Service für die Klasse " + serviceClass.getName() + " gefunden.");
        }
    }
}
