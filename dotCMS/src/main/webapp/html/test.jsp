<%@page import="com.dotcms.rest.api.v1.system.websocket.SystemEventsWebSocketEndPoint"%>
<%@page import="com.dotmarketing.business.APILocator"%>
<%APILocator.getWebSocketContainerAPI();  
final SystemEventsWebSocketEndPoint webSocketEndPoint = APILocator.getWebSocketContainerAPI()
.getEndpointInstance(SystemEventsWebSocketEndPoint.class);


%>
<%=webSocketEndPoint%>