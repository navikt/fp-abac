package no.nav.foreldrepenger.sikkerhet.abac.pdp2;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.MessageUtils;

public class CustomLoggingFeature extends LoggingFeature implements ClientRequestFilter, ClientResponseFilter {

    private static final boolean printEntity = true;
    private static final int maxEntitySize = 8 * 1024;
    private final Logger logger = Logger.getLogger("CustomLoggingFeature");
    private static final String ENTITY_LOGGER_PROPERTY = CustomLoggingFeature.class.getName();
    private static final String NOTIFICATION_PREFIX = "* ";
    private static final String REQUEST_PREFIX = "> ";
    private static final String RESPONSE_PREFIX = "< ";
    private static final String AUTHORIZATION = "Authorization";
    private static final String EQUAL = " = ";
    private static final String HEADERS_SEPARATOR = ", ";
    private static List<String> requestHeaders;

    static {
        requestHeaders = new ArrayList<>();
        requestHeaders.add(AUTHORIZATION);
    }

    public CustomLoggingFeature(LoggingFeature.LoggingFeatureBuilder builder) {
        super(builder);
    }

    public CustomLoggingFeature(Logger logger, Level level, Verbosity verbosity, Integer maxEntitySize) {
        super(logger, level, verbosity, maxEntitySize);
    }

    @Override
    public boolean configure(FeatureContext context) {
        context.register(this);
        return true;
    }

    @Override
    public void filter(final ClientRequestContext context) {
        final StringBuilder b = new StringBuilder();
        printHeaders(b, context.getStringHeaders());
        printRequestLine(b, "Sending client request", context.getMethod(), context.getUri());

        if (printEntity && context.hasEntity()) {
            final OutputStream stream = new LoggingStream(b, context.getEntityStream());
            context.setEntityStream(stream);
            context.setProperty(ENTITY_LOGGER_PROPERTY, stream);
            // not calling log(b) here - it will be called by the interceptor
        } else {
            log(b);
        }
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        final StringBuilder b = new StringBuilder();
        printResponseLine(b, "Client response received", responseContext.getStatus());

        if (printEntity && responseContext.hasEntity()) {
            responseContext.setEntityStream(logInboundEntity(b, responseContext.getEntityStream(),
                MessageUtils.getCharset(responseContext.getMediaType())));
        }
        log(b);
    }

    private static class LoggingStream extends FilterOutputStream {
        private final StringBuilder b;
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        LoggingStream(final StringBuilder b, final OutputStream inner) {
            super(inner);

            this.b = b;
        }

        public void write(final int i) throws IOException {
            if (byteArrayOutputStream.size() <= maxEntitySize) {
                byteArrayOutputStream.write(i);
            }
            out.write(i);
        }
    }

    private void printHeaders(StringBuilder b, MultivaluedMap<String, String> headers) {
        for (String header : requestHeaders) {
            if (Objects.nonNull(headers.get(header))) {
                b.append(header).append(EQUAL).append(headers.get(header)).append(HEADERS_SEPARATOR);
            }
        }
        int lastIndex = b.lastIndexOf(HEADERS_SEPARATOR);
        if (lastIndex != -1) {
            b.delete(lastIndex, lastIndex + HEADERS_SEPARATOR.length());
            b.append("\n");
        }
    }

    private void log(final StringBuilder b) {
        if (logger != null) {
            logger.info(b.toString());
        }
    }

    private void printRequestLine(final StringBuilder b, final String note, final String method, final URI uri) {
        b.append(NOTIFICATION_PREFIX)
            .append(note)
            .append(" on thread ").append(Thread.currentThread().getId())
            .append(REQUEST_PREFIX).append(method).append(" ")
            .append(uri.toASCIIString()).append("\n");
    }

    private void printResponseLine(final StringBuilder b, final String note, final int status) {
        b.append(NOTIFICATION_PREFIX)
            .append(note)
            .append(" on thread ").append(Thread.currentThread().getId())
            .append(RESPONSE_PREFIX)
            .append(status)
            .append("\n");
    }

    private InputStream logInboundEntity(final StringBuilder b, InputStream stream, final Charset charset) throws IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        stream.mark(maxEntitySize + 1);
        final byte[] entity = new byte[maxEntitySize + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, maxEntitySize), charset));
        if (entitySize > maxEntitySize) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }
}
