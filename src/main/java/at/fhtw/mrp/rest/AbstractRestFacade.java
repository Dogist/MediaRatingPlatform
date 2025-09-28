package at.fhtw.mrp.rest;

import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.*;
import at.fhtw.mrp.exceptions.InvalidInputException;
import at.fhtw.mrp.service.AuthService;
import at.fhtw.mrp.service.UserSessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Abstrakte Basis-Klasse für alle Facades welche die via {@link REST}-Annotations
 * deklarierten Methoden registriert und die Controller-Tätigkeiten für die
 * Request und Response Konvertierung übernimmt.
 */
public abstract class AbstractRestFacade implements HttpHandler {
    public static final Logger LOGGER = Logger.getLogger(AbstractRestFacade.class.getName());
    private static final boolean ENABLE_AUTHENTICATION = true;

    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final List<RequestMapping> requestMappings;
    private final String basePath;

    public AbstractRestFacade(String basePath) {
        authService = new AuthService();
        this.objectMapper = new ObjectMapper();
        this.requestMappings = new ArrayList<>();
        this.basePath = "/api/" + basePath;

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(REST.class)) {
                REST restA = declaredMethod.getAnnotation(REST.class);
                requestMappings.add(new RequestMapping(
                        getBasePath() + "/" + restA.path(),
                        restA.method(), declaredMethod,
                        restA.authRequired()));
            }
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            boolean responseHandled = false;
            for (RequestMapping requestMapping : requestMappings) {
                if (exchange.getRequestMethod().equals(requestMapping.httpMethod().name())
                        && requestMapping.pattern().asMatchPredicate().test(exchange.getRequestURI().getPath())) {
                    if (ENABLE_AUTHENTICATION && requestMapping.isAuthRequired()) {
                        String authorization = exchange.getRequestHeaders().getFirst("Authorization");
                        String loggedInUser = null;
                        // TODO auch Basic Auth?
                        if (authorization != null && authorization.startsWith("Bearer ")) {
                            String token = StringUtils.substringAfter(authorization, "Bearer ");
                            loggedInUser = authService.checkAuthToken(token);
                        }
                        if (loggedInUser != null) {
                            UserSessionService.registerUserSession(loggedInUser);
                        } else {
                            exchange.sendResponseHeaders(HttpStatus.UNAUTHORIZED.code, 0);
                            return;
                        }
                    }

                    List<Object> params = handleParameters(exchange, requestMapping);
                    try {
                        Object returnVal = requestMapping.method().invoke(this, params.toArray());
                        handleResponse(exchange, returnVal);
                        responseHandled = true;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        if (e.getCause() instanceof InvalidInputException) {
                            handleResponse(exchange, new Response(HttpStatus.BAD_REQUEST, ContentType.PLAIN_TEXT, e.getCause().getMessage()));
                            responseHandled = true;
                        } else {
                            throw new RuntimeException("Es gab einen Fehler!", e);
                        }
                    }
                }
            }
            if (!responseHandled) {
                exchange.sendResponseHeaders(HttpStatus.NOT_FOUND.code, 0);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception: ", e);
            exchange.sendResponseHeaders(HttpStatus.INTERNAL_SERVER_ERROR.code, 0);
        }
        finally {
            UserSessionService.clearUserSession();
        }
    }

    private void handleResponse(HttpExchange exchange, Object returnVal) throws IOException {
        exchange.getResponseHeaders().add("Cache-Control", "nocache");
        if (returnVal != null) {
            if (returnVal instanceof Response) {
                exchange.getResponseHeaders().add("Content-Type", ((Response) returnVal).contentType());
                byte[] responseBody = ((Response) returnVal).content().getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(((Response) returnVal).status(), responseBody.length);
                exchange.getResponseBody().write(responseBody);
            } else {
                exchange.getResponseHeaders().add("Content-Type", ContentType.JSON.type);
                objectMapper.writeValue(exchange.getResponseBody(), returnVal);
                exchange.sendResponseHeaders(HttpStatus.OK.code, 0);
            }
        } else {
            exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.code, 0);
        }
    }

    private List<Object> handleParameters(HttpExchange exchange, RequestMapping requestMapping) throws IOException {
        List<Object> params = new ArrayList<>();
        boolean bodyParamFound = false;
        Parameter[] parameters = requestMapping.method().getParameters();
        for (Parameter parameter : parameters) {
            PathParam pathParamA = parameter.getAnnotation(PathParam.class);
            QueryParam queryParamA = parameter.getAnnotation(QueryParam.class);
            if (!bodyParamFound && pathParamA == null && queryParamA == null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String bodyString = br.lines().collect(Collectors.joining());
                    if (!bodyString.isEmpty()) {
                        params.add(objectMapper.readValue(bodyString, parameter.getType()));
                    } else {
                        params.add(null);
                    }
                }
            } else if (pathParamA != null) {
                Matcher matcher = requestMapping.pattern().matcher(exchange.getRequestURI().getPath());
                if (matcher.find()) {
                    String p = matcher.group(pathParamA.value());
                    if (parameter.getType().isAssignableFrom(Integer.class)) {
                        params.add(Integer.parseInt(p));
                    } else {
                        params.add(p);
                    }
                }
            } else if (queryParamA != null) {
                Matcher matcher = Pattern.compile("[?&]" + queryParamA.value() + "=([^&]*)")
                        .matcher(exchange.getRequestURI().getQuery());
                if (matcher.find()) {
                    String p = matcher.group();
                    if (parameter.getType().isAssignableFrom(Integer.class)) {
                        params.add(Integer.parseInt(p));
                    } else {
                        params.add(p);
                    }
                }
            } else {
                params.add(null);
            }
        }
        return params;
    }

    public String getBasePath() {
        return basePath;
    }
}
