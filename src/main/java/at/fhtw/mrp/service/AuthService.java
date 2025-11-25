package at.fhtw.mrp.service;

import at.fhtw.mrp.dto.UserAuthDTO;
import at.fhtw.mrp.exceptions.InvalidInputException;

/**
 * Service für die Authentifizierung des Users.
 */
public interface AuthService {
    /**
     * MMethode welche versucht das übergebene {@link UserAuthDTO} zu authentifizieren.
     *
     * @param userAuth welcher authentifiziert wird.
     * @return String Bearer-Token, welcher für die Authentifizierung verwendet werden kann.
     */
    String loginUser(UserAuthDTO userAuth) throws InvalidInputException;

    /**
     * Methode welche einen Bearer-Token überprüft, und den User zurückgibt, wenn erfolgreich.
     *
     * @param token welche geprüft wird.
     * @return Benutzername des Users welcher authentifiziert wurde.
     */
    String checkAuthToken(String token);
}
