<%@page import="java.util.Iterator"%>
<%@page import="java.lang.management.ManagementFactory"%>
<%@page import="java.lang.management.RuntimeMXBean"%>
<%@page import="java.util.Properties"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Map"%>
<%@page import="com.liferay.portal.language.LanguageUtil"%>
<%@page import="com.dotmarketing.util.Config"%>

	        <table border="1" width="90%">
	            <thead>
	            <th width="130">
	                <%= LanguageUtil.get(pageContext, "Env-Variable") %>
	            </th>
	            <th>
	                <%= LanguageUtil.get(pageContext, "Value") %>
	            </th>
	            </thead>

	            <%Map<String,String> s = System.getenv();%>
	            <%TreeSet<Object> keys = new TreeSet(s.keySet()); %>
	            <%for(Object key : keys){ %>
	            <tr>
	                <td valign="top"><%=key %></td>
	                <td style="white-space: normal;word-wrap: break-word;"><%=s.get(key) %></td>
	            </tr>

	            <%} %>
	        </table>
		<br>&nbsp;<br>
	        <table border="1" width="90%">
	            <thead>
	            <th width="130">
	                <%= LanguageUtil.get(pageContext, "System-Property") %>
	            </th>
	            <th>
	                <%= LanguageUtil.get(pageContext, "Value") %>
	            </th>
	            </thead>

	            <%Properties p = System.getProperties();%>
	            <% RuntimeMXBean b = ManagementFactory.getRuntimeMXBean(); %>
	            <tr>
	                <td valign="top" style="vertical-align: top">Startup Args</td>
	                <td valign="top" style="vertical-align: top">
	                    <%for(Object key : b.getInputArguments()){ %>
	                    <%=key %><br>
	                    <%} %>
	                </td>
	            </tr>
				<%keys = new TreeSet(p.keySet()); %>
	            <%for(Object key : keys){ %>

	            <tr>
	                <td valign="top"><%=key %></td>
	                <td>
	                <div  style="white-space: normal;word-wrap: break-word !important;max-width: 400px">
	                	<%=p.get(key) %>
	                </div>
	                </td>
	            </tr>

	            <%} %>
	        </table>
	        <br>&nbsp;<br>

	        <table border="1" width="90%">
	            <thead>
	            <th width="130">
	                <%= LanguageUtil.get(pageContext, "dotmarketing.config") %>
	            </th>
	            <th>
	                <%= LanguageUtil.get(pageContext, "Value") %>
	            </th>
	            </thead>

	            <%Iterator<String> strings= Config.getKeys();
	            
	            while(strings.hasNext()){ 
	           	 String key = strings.next();
	            	String value=null;
	            	try{
	            	  value=Config.getStringProperty(key);
	            	}
	            	catch(Exception e){
	            	  value=e.getMessage();
	            	}
	            %>
	            <tr>
	                <td valign="top"><%=key %></td>
	                <td style="white-space: normal;word-wrap: break-word;"><%=value %></td>
	            </tr>

	            <%} %>
	        </table>
	        
	        