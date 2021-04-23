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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.message.MessageUtils;
import org.slf4j.LoggerFactory;

public class CustomLoggingFeature extends LoggingFeature implements ContainerRequestFilter, ContainerResponseFilter,
    ClientRequestFilter, ClientResponseFilter, WriterInterceptor {

    org.slf4j.Logger LOG = LoggerFactory.getLogger(CustomLoggingFeature.class);

    private static final boolean PRINT_ENTITY = true;
    private static final int MAX_ENTITY_SIZE = 8 * 1024;
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
        LOG.trace("Filter request.");
        final var b = new StringBuilder();
        printHeaders(b, context.getStringHeaders());
        printRequestLine(b, "Sending client request", context.getMethod(), context.getUri());

        if (PRINT_ENTITY && context.hasEntity()) {
            final OutputStream stream = new LoggingStream(b, context.getEntityStream());
            context.setEntityStream(stream);
            context.setProperty(ENTITY_LOGGER_PROPERTY, stream);
            // not calling log(b) here - it will be called by the interceptor
        }
        log(b);
        LOG.debug(b.toString());
    }

    @Override
    public void aroundWriteTo(final WriterInterceptorContext writerInterceptorContext) throws IOException, WebApplicationException {
        LOG.trace("Log around.");
        final LoggingStream stream = (LoggingStream) writerInterceptorContext.getProperty(ENTITY_LOGGER_PROPERTY);
        writerInterceptorContext.proceed();
        if (stream != null) {
            var stringBuilder = stream.getStringBuilder(MessageUtils.getCharset(writerInterceptorContext.getMediaType()));
            log(stringBuilder);
            LOG.debug(stringBuilder.toString());
        }
    }

    @Override
    public void filter(final ClientRequestContext requestContext, final ClientResponseContext responseContext) throws IOException {
        LOG.trace("Filter response.");
        final StringBuilder b = new StringBuilder();
        printResponseLine(b, "Client response received", responseContext.getStatus());

        if (PRINT_ENTITY && responseContext.hasEntity()) {
            responseContext.setEntityStream(logInboundEntity(b, responseContext.getEntityStream(),
                MessageUtils.getCharset(responseContext.getMediaType())));
        }
        log(b);
        LOG.debug(b.toString());
    }

    @Override
    public void filter(final ContainerRequestContext context) throws IOException {
        LOG.trace("Filter response.");
        final StringBuilder b = new StringBuilder();
        printHeaders(b, context.getHeaders());
        printRequestLine(b, "Server has received a request", context.getMethod(), context.getUriInfo().getRequestUri());

        if (PRINT_ENTITY && context.hasEntity()) {
            context.setEntityStream(logInboundEntity(b, context.getEntityStream(), MessageUtils.getCharset(context.getMediaType())));
        }
        log(b);
        LOG.debug(b.toString());
    }

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) {
        final StringBuilder b = new StringBuilder();
        printResponseLine(b, "Server responded with a response", responseContext.getStatus());

        if (PRINT_ENTITY && responseContext.hasEntity()) {
            final OutputStream stream = new LoggingStream(b, responseContext.getEntityStream());
            responseContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
            // not calling log(b) here - it will be called by the interceptor
            log(b);
            LOG.debug(b.toString());
        } else {
            log(b);
            LOG.debug(b.toString());
        }
    }

    private static class LoggingStream extends FilterOutputStream {
        private final StringBuilder b;
        private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        LoggingStream(final StringBuilder b, final OutputStream inner) {
            super(inner);

            this.b = b;
        }

        StringBuilder getStringBuilder(Charset charset) {
            // write entity to the builder
            final byte[] entity = byteArrayOutputStream.toByteArray();

            b.append(new String(entity, 0, Math.min(entity.length, MAX_ENTITY_SIZE), charset));
            if (entity.length > MAX_ENTITY_SIZE) {
                b.append("...more...");
            }
            b.append('\n');

            return b;
        }

        @Override
        public void write(final int i) throws IOException {
            if (byteArrayOutputStream.size() <= MAX_ENTITY_SIZE) {
                byteArrayOutputStream.write(i);
            }
            out.write(i);
        }

        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            write(b);
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
            logger.log(Level.INFO, b.toString());
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
        stream.mark(MAX_ENTITY_SIZE + 1);
        final var entity = new byte[MAX_ENTITY_SIZE + 1];
        final int entitySize = stream.read(entity);
        b.append(new String(entity, 0, Math.min(entitySize, MAX_ENTITY_SIZE), charset));
        if (entitySize > MAX_ENTITY_SIZE) {
            b.append("...more...");
        }
        b.append('\n');
        stream.reset();
        return stream;
    }
}
