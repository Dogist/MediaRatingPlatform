package at.fhtw.mrp.rest.server;

import at.fhtw.mrp.rest.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.regex.Pattern;

public final class RequestMapping {
    private final String path;
    private final HttpMethod httpMethod;
    private final Method method;
    private final Pattern pattern;
    private final boolean authRequired;

    public RequestMapping(String path, HttpMethod httpMethod, Method method, boolean authRequired) {
        assert path != null;
        assert httpMethod != null;
        assert method != null;

        this.path = path;
        this.method = method;
        this.httpMethod = httpMethod;
        this.authRequired = authRequired;
        String regexPath = path.replaceAll("\\{(\\w+)}", "(?<$1>\\\\w+)");
        this.pattern = Pattern.compile(regexPath);
    }

    public String path() {
        return path;
    }

    public HttpMethod httpMethod() {
        return httpMethod;
    }

    public Method method() {
        return method;
    }

    public Pattern pattern() {
        return pattern;
    }

    public boolean isAuthRequired() {
        return authRequired;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (RequestMapping) obj;
        return Objects.equals(this.path, that.path) &&
                Objects.equals(this.httpMethod, that.httpMethod) &&
                Objects.equals(this.method, that.method) &&
                Objects.equals(this.pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, httpMethod, method, pattern);
    }

    @Override
    public String toString() {
        return "RequestMapping[" +
                "path=" + path + ", " +
                "httpMethod=" + httpMethod + ", " +
                "method=" + method + ", " +
                "pattern=" + pattern + ']';
    }

}
