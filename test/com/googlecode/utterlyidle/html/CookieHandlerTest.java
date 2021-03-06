package com.googlecode.utterlyidle.html;

import com.googlecode.utterlyidle.HttpHandler;
import com.googlecode.utterlyidle.HttpHeaders;
import com.googlecode.utterlyidle.cookies.Cookie;
import com.googlecode.utterlyidle.handlers.RecordingHttpHandler;
import org.junit.Test;

import static com.googlecode.totallylazy.matchers.Matchers.is;
import static com.googlecode.utterlyidle.RequestBuilder.get;
import static com.googlecode.utterlyidle.ResponseBuilder.response;
import static com.googlecode.utterlyidle.handlers.RecordingHttpHandler.recordingHttpHandler;
import static com.googlecode.utterlyidle.handlers.ReturnResponseHandler.returnsResponse;
import static org.hamcrest.MatcherAssert.assertThat;

public class CookieHandlerTest {
    @Test
    public void setsCookiesOnFollowingRequests() throws Exception {
        Cookie cookie = new Cookie("user", "dan");
        RecordingHttpHandler recording = recordingHttpHandler(returnsResponse(response().cookie(cookie)));
        HttpHandler handler = new CookieHandler(recording);

        handler.handle(get("no-cookie").build());
        assertThat(recording.lastRequest().headers().contains(HttpHeaders.COOKIE), is(false));
        handler.handle(get("should-have-cookie").build());
        assertThat(recording.lastRequest().headers().getValue(HttpHeaders.COOKIE), is(cookie.toString()));
    }
}
