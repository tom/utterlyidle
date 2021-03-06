package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.First;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Predicate;
import com.googlecode.totallylazy.Second;
import com.googlecode.totallylazy.Sequences;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.totallylazy.time.Dates;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.cookies.CookieAttribute;

import java.util.Date;
import java.util.List;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Callables.second;
import static com.googlecode.totallylazy.Pair.pair;
import static com.googlecode.totallylazy.Predicates.and;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static com.googlecode.totallylazy.Strings.startsWith;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.expires;
import static com.googlecode.utterlyidle.cookies.CookieAttribute.maxAge;
import static com.googlecode.utterlyidle.cookies.CookieParameters.toHttpHeader;

public class ResponseBuilder {
    private Status status;
    private List<Pair<String, String>> headers;
    private Entity entity;

    public ResponseBuilder(Status status, Iterable<Pair<String, String>> headers, Entity entity) {
        this.status = status;
        this.headers = sequence(headers).toList();
        this.entity = entity;
    }

    public static ResponseBuilder response(Status status) {
        return new ResponseBuilder(status, Sequences.<Pair<String, String>>empty(), Entity.empty());
    }

    public static ResponseBuilder response() {
        return new ResponseBuilder(Status.OK, Sequences.<Pair<String, String>>empty(), Entity.empty());
    }

    public static ResponseBuilder modify(Response response) {
        return new ResponseBuilder(response.status(), response.headers(), response.entity());
    }

    public ResponseBuilder status(Status status) {
        this.status = status;
        return this;
    }

    public ResponseBuilder header(String name, Object value) {
        if (value == null) return this;
        if (value instanceof Date) return header(name, Dates.RFC822().format((Date) value));
        headers.add(pair(name, value.toString()));
        return this;
    }

    public ResponseBuilder cookie(Cookie cookie) {
        return header(HttpHeaders.SET_COOKIE, toHttpHeader(cookie));
    }

    public ResponseBuilder removeCookie(String name) {
        return removeHeaders(HttpHeaders.SET_COOKIE, startsWith(name + "=")).
            cookie(Cookie.cookie(name, "", maxAge(0), expires(new Date(0))));
    }

    public Response build() {
        return Responses.response(status, HeaderParameters.headerParameters(headers), entity);
    }


    public ResponseBuilder removeHeaders(String name) {
        RequestBuilder.removeHeaders(headers, name);
        return this;
    }

    public ResponseBuilder removeHeaders(String name, Predicate<String> valuePredicate) {
        Predicate<Pair<String, String>> nameP = where(first(String.class), is(name));
        Predicate<Pair<String, String>> valueP = where(second(String.class), valuePredicate);
        headers = sequence(headers).filter(not(and(nameP, valueP))).toList();
        return this;
    }

    public ResponseBuilder entity(Object value) {
        if(value instanceof Entity) return entity((Entity) value);
        return entity(Entity.entity(value));
    }

    public ResponseBuilder entity(Entity value) {
        entity = value;
        return this;
    }

    public ResponseBuilder removeEntity() {
        entity = Entity.empty();
        return this;
    }

    public ResponseBuilder replaceHeaders(String name, Object value) {
        return removeHeaders(name).header(name, value);
    }

    public ResponseBuilder contentType(String contentType) {
        return header(HttpHeaders.CONTENT_TYPE, contentType);
    }
}
