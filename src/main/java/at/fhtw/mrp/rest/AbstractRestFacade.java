package at.fhtw.mrp.rest;

import at.fhtw.mrp.dto.Validateable;
import at.fhtw.mrp.exceptions.BaseMRPException;
import at.fhtw.mrp.rest.http.ContentType;
import at.fhtw.mrp.rest.http.HttpStatus;
import at.fhtw.mrp.rest.server.*;
import at.fhtw.mrp.service.AuthService;
import at.fhtw.mrp.service.BearerAuthServiceImpl;
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
        authService = new BearerAuthServiceImpl();
        // TODO diesen auf einen Service auslagern
        this.objectMapper = new ObjectMapper();
        this.requestMappings = new ArrayList<>();
        this.basePath = "/api/" + basePath;

        for (Method declaredMethod : getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(REST.class)) {
                REST restA = declaredMethod.getAnnotation(REST.class);
                requestMappings.add(new RequestMapping(
                        StringUtils.stripEnd(getBasePath() + "/" + restA.path(), "/"),
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

                    try {
                        List<Object> params = handleParameters(exchange, requestMapping);

                        Object returnVal = requestMapping.method().invoke(this, params.toArray());
                        handleResponse(exchange, returnVal);
                        responseHandled = true;
                        break;
                    } catch (IllegalAccessException | InvocationTargetException | BaseMRPException e) {
                        BaseMRPException customException = null;
                        if (e instanceof BaseMRPException) {
                            customException = (BaseMRPException) e;
                        } else if (e.getCause() instanceof BaseMRPException) {
                            customException = (BaseMRPException) e.getCause();
                        }

                        if (customException != null) {
                            handleResponse(exchange, new Response(customException.getStatus(), ContentType.PLAIN_TEXT, customException.getMessage()));
                            responseHandled = true;
                            break;
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
        } finally {
            UserSessionService.clearUserSession();
        }
    }

    private void handleResponse(HttpExchange exchange, Object returnVal) throws IOException {
        exchange.getResponseHeaders().add("Cache-Control", "nocache");
        if (returnVal != null) {
            if (returnVal instanceof Response(int status, String contentType, Object content)) {
                if (content == null) {
                    exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.code, -1);
                } else if (content instanceof String) {
                    exchange.getResponseHeaders().add("Content-Type", contentType);
                    byte[] responseBody = ((String) content).getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(status, responseBody.length);
                    exchange.getResponseBody().write(responseBody);
                } else if (contentType.equals(ContentType.JSON.type)) {
                    exchange.getResponseHeaders().add("Content-Type", ContentType.JSON.type);
                    exchange.sendResponseHeaders(status, 0);
                    objectMapper.writeValue(exchange.getResponseBody(), content);
                } else {
                    throw new IllegalArgumentException("Diese Kombination von Response-Content und Content-Type wird nicht unterstützt!");
                }
            } else {
                exchange.getResponseHeaders().add("Content-Type", ContentType.JSON.type);
                exchange.sendResponseHeaders(HttpStatus.OK.code, 0);
                objectMapper.writeValue(exchange.getResponseBody(), returnVal);
            }
        } else {
            exchange.sendResponseHeaders(HttpStatus.NO_CONTENT.code, -1);
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
                        Object o = objectMapper.readValue(bodyString, parameter.getType());
                        if (o instanceof Validateable) {
                            ((Validateable) o).validate();
                        }
                        params.add(o);
                    } else {
                        params.add(null);
                    }
                }
            } else if (pathParamA != null) {
                Matcher matcher = requestMapping.pattern().matcher(exchange.getRequestURI().getPath());
                if (matcher.find()) {
                    String p = matcher.group(pathParamA.value());
                    if (parameter.getType().isAssignableFrom(Long.class)) {
                        params.add(Long.parseLong(p));
                    } else if (parameter.getType().isAssignableFrom(Integer.class)) {
                        params.add(Integer.parseInt(p));
                    } else {
                        params.add(p);
                    }
                }
            } else if (queryParamA != null) {
                Matcher matcher = Pattern.compile("(?:^|&)" + queryParamA.value() + "=([^&]*)")
                        .matcher(exchange.getRequestURI().getQuery());
                if (matcher.find()) {
                    String p = matcher.group(1);
                    if (parameter.getType().isAssignableFrom(Long.class)) {
                        params.add(Long.parseLong(p));
                    } else if (parameter.getType().isAssignableFrom(Integer.class)) {
                        params.add(Integer.parseInt(p));
                    } else {
                        params.add(p);
                    }
                } else {
                    params.add(null);
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
