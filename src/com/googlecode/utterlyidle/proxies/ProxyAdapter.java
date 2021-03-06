package com.googlecode.utterlyidle.proxies;

import com.googlecode.totallylazy.Mapper;
import com.googlecode.totallylazy.Option;
import com.googlecode.totallylazy.Uri;

import java.net.Proxy;
import java.net.ProxySelector;

import static com.googlecode.totallylazy.Predicates.is;
import static com.googlecode.totallylazy.Predicates.not;
import static com.googlecode.totallylazy.Predicates.where;
import static com.googlecode.totallylazy.Sequences.sequence;
import static java.net.Proxy.Type.DIRECT;
import static java.net.ProxySelector.getDefault;

public class ProxyAdapter implements ProxyFor {
    private final ProxySelector selector;

    private ProxyAdapter(final ProxySelector selector) {
        this.selector = selector;
    }

    public static ProxyFor proxyAdapter(final ProxySelector selector) {
        return new ProxyAdapter(selector);
    }

    public static ProxyFor systemProxy() {
        return HttpProxy.httpProxy(proxyAdapter(getDefault()));
    }

    @Override
    public Option<Proxy> proxyFor(final Uri uri) {
        return sequence(selector.select(uri.toURI())).find(where(type, is(not(DIRECT))));
    }

    public static Mapper<Proxy, Proxy.Type> type = new Mapper<Proxy, Proxy.Type>() {
        @Override
        public Proxy.Type call(final Proxy proxy) throws Exception {
            return proxy.type();
        }
    };

    public ProxySelector selector() {
        return selector;
    }
}
