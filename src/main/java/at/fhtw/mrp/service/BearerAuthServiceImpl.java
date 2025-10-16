package at.fhtw.mrp.service;

import at.fhtw.mrp.dao.UserDao;
import at.fhtw.mrp.dao.UserDaoImpl;
import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.exceptions.InvalidInputException;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service für die Authentifizierung des Users.
 * TODO wegen Synchronität prüfen. Semaphores?
 */
public class BearerAuthServiceImpl implements AuthService {
    /**
     * Auth-Token Lifetime in Minuten.
     */
    public static final int TOKEN_LIFETIME = 60;

    private final UserDao userDao;
    private static final List<AuthToken> activeAuthTokens;
    private static final Set<String> currentBearerTokens;

    static {
        activeAuthTokens = Collections.synchronizedList(new ArrayList<>());
        currentBearerTokens = Collections.synchronizedSet(new HashSet<>());
    }

    public BearerAuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;

    }

    /**
     * MMethode welche versucht das übergebene {@link UserAuthDTO} zu authentifizieren.
     *
     * @param userAuth welcher authentifiziert wird.
     * @return String Bearer-Token, welcher für die Authentifizierung verwendet werden kann.
     */
    @Override
    public String loginUser(UserAuthDTO userAuth) throws InvalidInputException {
        if (userAuth == null) {
            throw new InvalidInputException("Die Benutzerdaten sind unvollständig.");
        }
        // TODO Hash Password
        if (userDao.checkUserAuth(userAuth.username(), userAuth.password())) {
            synchronized (BearerAuthServiceImpl.class) {
                String token = UUID.randomUUID().toString();
                while (currentBearerTokens.contains(token)) {
                    token = UUID.randomUUID().toString();
                }
                currentBearerTokens.add(token);
                activeAuthTokens.add(new AuthToken(
                        token,
                        userAuth.username(),
                        LocalDateTime.now().plusMinutes(TOKEN_LIFETIME)));
                return token;
            }
        }
        throw new InvalidInputException("Der angegebene Benutzer konnte nicht angemeldet werden.");
    }

    /**
     * Methode welche einen Bearer-Token überprüft, und den User zurückgibt, wenn erfolgreich.
     *
     * @param token welche geprüft wird.
     * @return Benutzername des Users welcher authentifiziert wurde.
     */
    @Override
    public String checkAuthToken(String token) {
        String foundUser = null;
        if (currentBearerTokens.contains(token)) {
            synchronized (BearerAuthServiceImpl.class) {
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
            }
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
