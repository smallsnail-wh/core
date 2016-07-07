package com.dotcms.rest.api.v1.authentication;

import com.dotcms.repackage.javax.ws.rs.core.Response;
import com.dotcms.repackage.org.apache.struts.Globals;
import com.dotcms.rest.ErrorEntity;
import com.dotcms.rest.ResponseEntityView;
import com.dotmarketing.util.SecurityLogger;
import com.liferay.portal.language.LanguageException;
import com.liferay.portal.language.LanguageUtil;
import org.mockito.cglib.core.Local;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Locale;

/**
 * Just a helper to encapsulate AuthenticationResource functionality.
 * @author jsanca
 */
public class AuthenticationHelper {

    public static AuthenticationHelper INSTANCE =
            new AuthenticationHelper();

    private AuthenticationHelper () {}

    /**
     * Get Error response based on a status and message key
     * This support is a single message
     *
     * @param request
     * @param status
     * @param userId
     * @param messageKey
     * @return Response
     */
    public Response getErrorResponse(final HttpServletRequest request,
                                     final Response.Status status,
                                     final Locale locale,
                                     final String userId,
                                     final String messageKey) {

        try {

            SecurityLogger.logInfo(this.getClass(), "An invalid attempt to login as " + userId.toLowerCase() + " has been made from IP: " + request.getRemoteAddr());

            return Response.status(status).entity
                (new ResponseEntityView
                    (Arrays.asList(new ErrorEntity(messageKey,
                            LanguageUtil.get(locale,
                                    messageKey))))).build();


        } catch (LanguageException e1) {
            // Quiet
        }

        return null;
    }
}
