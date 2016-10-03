package info.smart_tools.smartactors.core;

import info.smart_tools.smartactors.endpoint.endpoint_channel_inbound_handler.EndpointChannelInboundHandler;
import info.smart_tools.smartactors.core.http_request_handler.HttpRequestHandler;
import info.smart_tools.smartactors.core.http_server.HttpServer;
import info.smart_tools.smartactors.endpoint.interfaces.ienvironment_handler.IEnvironmentHandler;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * Endpoint for http connection
 */
public class HttpEndpoint extends HttpServer {
    /**
     * Constructor for endpoint
     *
     * @param port             port of the endpoint
     * @param maxContentLength max length of the content
     * @param scope            scope for endpoint
     * @param handler          handler for environment
     * @param receiverChain    chain, that should receive {@link io.netty.channel.ChannelOutboundBuffer.MessageProcessor}
     * @param name             name of the endpoint
     * @throws ResolutionException if IOC cant resolve smth

     */
    public HttpEndpoint(final int port, final int maxContentLength, final IScope scope,
                        final IEnvironmentHandler handler, final IReceiverChain receiverChain,
                        final String name
    ) throws ResolutionException {
        super(port, maxContentLength, new EndpointChannelInboundHandler<>(
                new HttpRequestHandler(scope, handler, receiverChain, name),
                FullHttpRequest.class
        ));
    }
}
