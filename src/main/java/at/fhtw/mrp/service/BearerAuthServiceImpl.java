package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.entity.UserEntity;
import at.fhtw.mrp.exceptions.InvalidInputException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class BearerAuthServiceImpl implements AuthService {
    /**
     * Auth-Token Lifetime in Minuten.
     */
    public static final int TOKEN_LIFETIME = 60;

    private final UserDao userDao;
    private static final List<AuthToken> activeAuthTokens = new ArrayList<>();
    private static final Set<String> currentBearerTokens = new HashSet<>();
    private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public BearerAuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;

    }

    @Override
    public String loginUser(UserAuthDTO userAuth) throws InvalidInputException {
        if (userAuth == null) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        UserEntity user = userDao.getUserByUsername(userAuth.username());

        if (user != null && HashUtil.checkHashedPassword(user.getPassword(), userAuth.password())) {
            Lock lock = readWriteLock.writeLock();
            lock.lock();
            String token = UUID.randomUUID().toString();
            while (currentBearerTokens.contains(token)) {
                token = UUID.randomUUID().toString();
            }
            currentBearerTokens.add(token);
            activeAuthTokens.add(new AuthToken(
                    token,
                    userAuth.username(),
                    LocalDateTime.now().plusMinutes(TOKEN_LIFETIME)));
            lock.unlock();
            return token;
        }
        throw new InvalidInputException("Der angegebene Benutzer konnte nicht angemeldet werden.");
    }

    @Override
    public String checkAuthToken(String token) {
        String foundUser = null;
        Lock readLock = readWriteLock.readLock();
        try {
            readLock.lock();
            if (!currentBearerTokens.contains(token)) {
                return null;
            }
        } finally {
            readLock.unlock();
        }
        Lock writeLock = readWriteLock.writeLock();
        try {
            writeLock.lock();
            for (Iterator<AuthToken> iterator = activeAuthTokens.iterator(); iterator.hasNext(); ) {
                AuthToken activeAuthToken = iterator.next();
                if (activeAuthToken.expiration.isBefore(LocalDateTime.now())) {
                    iterator.remove();
                    currentBearerTokens.remove(activeAuthToken.token);
                } else if (activeAuthToken.token.equals(token)) {
                    foundUser = activeAuthToken.username;
                    // Token erweitern
                    activeAuthToken.setExpiration(LocalDateTime.now().plusMinutes(TOKEN_LIFETIME));
                }
            }
        } finally {
            writeLock.unlock();
        }
        return foundUser;
    }

    private static final class AuthToken {
        private final String token;
        private final String username;
        private LocalDateTime expiration;

        private AuthToken(String token, String username, LocalDateTime expiration) {
            this.token = token;
            this.username = username;
            this.expiration = expiration;
        }

        public String getToken() {
            return token;
        }

        public String getUsername() {
            return username;
        }

        public LocalDateTime getExpiration() {
            return expiration;
        }

        public void setExpiration(LocalDateTime expiration) {
            this.expiration = expiration;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (AuthToken) obj;
            return Objects.equals(this.token, that.token) &&
                    Objects.equals(this.username, that.username) &&
                    Objects.equals(this.expiration, that.expiration);
        }

        @Override
        public int hashCode() {
            return Objects.hash(token, username, expiration);
        }

        @Override
        public String toString() {
            return "AuthToken[" +
                    "token=" + token + ", " +
                    "username=" + username + ", " +
                    "expiration=" + expiration + ']';
        }


    }
}
