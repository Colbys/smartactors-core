package info.smart_tools.smartactors.http_endpoint.http_request_sender_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint.interfaces.ichannel_handler.IChannelHandler;
import info.smart_tools.smartactors.endpoint.interfaces.iclient.IClient;
import info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.exception.HttpRequestSenderActorException;
import info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.wrappers.HttpRequestSenderActorWrapper;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject_wrapper.IObjectWrapper;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Actor for sending request by http
 * <p>
 * Request example
 * <pre>
 * "request": {
 * "uuid": "some_uuid",
 * "uri": "http://uri.for.request/something",
 * "method": "POST",
 * "timeout": 100, after this timeout to "exceptionalMessageMapId" will send message with full request
 * "exceptionalMessageMapId": "SelectChain",
 * "messageMapId": "sendRequest", start chain for response
 * "content": {
 * "hello": "world"
 * }
 * }
 * </pre>
 */
public class HttpRequestSenderActor {

    private IFieldName uriFieldName;

    /**
     * Constructor for actor
     */
    public HttpRequestSenderActor() {
        try {
            uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
        } catch (ResolutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler of the actor for send response
     *
     * @param wrapper Wrapper of the actor
     * @throws HttpRequestSenderActorException if there are some problems on sending http request
     */
    public void sendRequest(final HttpRequestSenderActorWrapper wrapper)
            throws HttpRequestSenderActorException {
        try {
            IObjectWrapper objectWrapper = (IObjectWrapper) wrapper;
            IObject request = wrapper.getRequest();
            if (request.getValue(uriFieldName).toString().startsWith("http:")) {
                uriFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "uri");
                if (request.getValue(uriFieldName) != null) {
                    IClient client = IOC.resolve(Keys.getOrAdd("getHttpClient"), request);
                    IOC.resolve(Keys.getOrAdd("sendHttpRequest"), client, request);
                }
            }
        } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
            throw new HttpRequestSenderActorException(e);
        }
    }

}
