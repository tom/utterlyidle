package com.googlecode.utterlyidle;

import com.googlecode.totallylazy.Callable1;
import com.googlecode.totallylazy.Function1;
import com.googlecode.totallylazy.Pair;
import com.googlecode.totallylazy.Uri;
import com.googlecode.totallylazy.predicates.LogicalPredicate;
import com.googlecode.utterlyidle.cookies.CookieParameters;
import com.googlecode.utterlyidle.io.HierarchicalPath;

import static com.googlecode.totallylazy.Callables.first;
import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.where;

public class Requests {
    public static Request request(String method, Uri requestUri, HeaderParameters headers, Object input) {
        return MemoryRequest.memoryRequest(method, requestUri, headers, Entity.entity(input));
    }

    public static Request request(String method, String path, QueryParameters query, HeaderParameters headers, Object input) {
        return request(method, Uri.uri(path + query.toString()), headers, input);
    }

    public static Callable1<Request, String> pathAsString() {
        return new Callable1<Request, String>() {
            @Override
            public String call(Request request) throws Exception {
                return request.uri().path();
            }
        };
    }

    public static Callable1<Request, Uri> uri() {
        return new Callable1<Request, Uri>() {
            public Uri call(Request request) throws Exception {
                return request.uri();
            }
        };
    }

    public static Callable1<Request, String> method() {
        return new Callable1<Request, String>() {
            public String call(Request request) throws Exception {
                return request.method();
            }
        };
    }

    public static Callable1<Request, QueryParameters> query() {
        return new Callable1<Request, QueryParameters>() {
            public QueryParameters call(Request request) throws Exception {
                return query(request);
            }
        };
    }

    public static Callable1<Request, String> queryParameter(final String name) {
        return new Callable1<Request, String>() {
            @Override
            public String call(Request request) throws Exception {
                return query(request).getValue(name);
            }
        };
    }

    public static LogicalPredicate<Request> hasQueryParameter(final String parameter) {
        return new LogicalPredicate<Request>() {
            @Override
            public boolean matches(Request request) {
                return query(request).contains(parameter);
            }
        };
    }

    public static Function1<Request, Accept> accept() {
        return new Function1<Request, Accept>() {
            public Accept call(Request request) throws Exception {
                return Accept.accept(request);
            }
        };
    }

    public static QueryParameters query(Request request) {
        return QueryParameters.parse(request.uri().query());
    }

    public static Callable1<Request, FormParameters> form() {
        return new Callable1<Request, FormParameters>() {
            public FormParameters call(Request request) throws Exception {
                return form(request);
            }
        };
    }

    public static FormParameters form(Request request) {
        String contentType = request.headers().getValue(HttpHeaders.CONTENT_TYPE);
        if (contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED)) {
            return FormParameters.parse(request.entity());
        } else {
            return FormParameters.formParameters();
        }
    }

    public static CookieParameters cookies(Request request) {
        return CookieParameters.cookies(request.headers());
    }

    public static Callable1<Request, HierarchicalPath> path() {
        return new Callable1<Request, HierarchicalPath>() {
            public HierarchicalPath call(Request request) throws Exception {
                return HierarchicalPath.hierarchicalPath(request.uri().path());
            }
        };
    }

    public static Callable1<Request, Entity> input() {
        return new Callable1<Request, Entity>() {
            public Entity call(Request request) throws Exception {
                return request.entity();
            }
        };
    }

    public static LogicalPredicate<Pair<Request, Response>> method(final String method) {
        return where(first(Request.class), where(Requests.method(), is(method)));
    }
}
