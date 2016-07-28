package info.smart_tools.smartactors.core.https_server;

import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.ssl_context_provider.SSLContextProvider;
import info.smart_tools.smartactors.core.ssl_context_provider.exceptions.SSLContextProviderException;
import info.smart_tools.smartactors.core.tcp_server.TcpServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLEngine;

/**
 * A server which handles HTTPS requests.
 */
public class HttpsServer extends TcpServer {

    private SSLContextProvider contextProvider;
    private final int maxContentLength;
    private boolean sslEnable = false;

    /**
     * Constructor
     *
     * @param port             port of the tcp server
     * @param requestHandler   channel for tcp server
     * @param maxContentLength max length of the content
     */
    public HttpsServer(final int port, final ChannelInboundHandler requestHandler, final int maxContentLength) {
        super(port, requestHandler);
        this.maxContentLength = maxContentLength;
    }

    /**
     * Method for turning on https
     *
     * @param contextProvider context provider for ssl
     */
    public void setSSL(final SSLContextProvider contextProvider) {
        this.contextProvider = contextProvider;
        sslEnable = true;
    }

    @Override
    protected ChannelPipeline setupPipeline(final ChannelPipeline pipeline) {
        if (sslEnable) {
            SSLEngine sslEngine = null;
            try {
                sslEngine = contextProvider.get().createSSLEngine();
            } catch (SSLContextProviderException ignored) {
            }

            sslEngine.setUseClientMode(false);

            return pipeline.addLast(
                    new HttpServerCodec(),
                    new HttpObjectAggregator(maxContentLength)
            ).addFirst("ssl", new SslHandler(sslEngine));
        }

        return pipeline.addLast(
                new HttpServerCodec(),
                new HttpObjectAggregator(maxContentLength)
        );
    }
}