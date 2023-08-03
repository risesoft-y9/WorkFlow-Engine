/*
 * $Header: /home/cvsroot/RiseNet/src/filters/SetCharacterEncodingFilter.java,v 1.1 2004/02/16 03:24:07 hongxing Exp $
 * $Revision: 1.2 $ $Date: 2011/01/07 02:00:52 $ ====================================================================
 * The Apache Software License, Version 1.1 Copyright (c) 1999-2001 The Apache Software Foundation. All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met: 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer. 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. The end-user documentation included with the redistribution, if any, must include the following
 * acknowlegement:
 * "This product includes software developed by the Apache Software Foundation (http://www.apache.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such third-party acknowlegements normally
 * appear. 4. The names "The Jakarta Project", "Tomcat", and "Apache Software Foundation" must not be used to endorse or
 * promote products derived from this software without prior written permission. For written permission, please contact
 * apache@apache.org. 5. Products derived from this software may not be called "Apache" nor may "Apache" appear in their
 * names without prior written permission of the Apache Group. THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ==================================================================== This software consists of voluntary
 * contributions made by many individuals on behalf of the Apache Software Foundation. For more information on the
 * Apache Software Foundation, please see <http://www.apache.org/>. [Additional notices, if required by prior licensing
 * conditions]
 */
package filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.risesoft.consts.UtilConsts;

/**
 * <p>
 * Example filter that sets the character encoding to be used in parsing the incoming request, either unconditionally or
 * only if the client did not specify a character encoding. Configuration of this filter is based on the following
 * initialization parameters:
 * </p>
 * <ul>
 * <li><strong>encoding </strong>- The character encoding to be configured for this request, either conditionally or
 * unconditionally based on the <code>ignore</code> initialization parameter. This parameter is required, so there is no
 * default.</li>
 * <li><strong>ignore </strong>- If set to "true", any character encoding specified by the client is ignored, and the
 * value returned by the <code>selectEncoding()</code> method is set. If set to "false, <code>selectEncoding()</code> is
 * called <strong>only </strong> if the client has not already specified an encoding. By default, this parameter is set
 * to "true".</li>
 * </ul>
 * <p>
 * Although this filter can be used unchanged, it is also easy to subclass it and make the <code>selectEncoding()</code>
 * method more intelligent about what encoding to choose, based on characteristics of the incoming request (such as the
 * values of the <code>Accept-Language</code> and <code>User-Agent</code> headers, or a value stashed in the current
 * user's session.
 * </p>
 * 
 * @author Craig McClanahan
 * @version $Revision: 1.2 $ $Date: 2011/01/07 02:00:52 $
 */
public class SetCharacterEncodingFilter implements Filter {

    // ----------------------------------------------------- Instance Variables
    /**
     * The default character encoding to set for requests that pass through this filter.
     */
    protected String encoding = null;
    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not
     * currently configured.
     */
    protected FilterConfig filterConfig = null;
    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    // --------------------------------------------------------- Public Methods
    /**
     * Take this filter out of service.
     */
    @Override
    public void destroy() {
        this.encoding = null;
        this.filterConfig = null;
    }

    /**
     * Select and set (if specified) the character encoding to be used to interpret request parameters for this request.
     * 
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            String encoding = selectEncoding(request);
            if (encoding != null) {
                request.setCharacterEncoding(this.encoding);
                response.setCharacterEncoding(this.encoding);
            }
        }
        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    /**
     * Place this filter into service.
     * 
     * @param filterConfig The filter configuration object
     */
    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        if (value == null) {
            this.ignore = true;
        } else if (value.equalsIgnoreCase(UtilConsts.TRUE)) {
            this.ignore = true;
        } else if (value.equalsIgnoreCase(UtilConsts.YES)) {
            this.ignore = true;
        } else {
            this.ignore = false;
        }
    }

    // ------------------------------------------------------ Protected Methods
    /**
     * Select an appropriate character encoding to be used, based on the characteristics of the current request and/or
     * filter initialization parameters. If no character encoding should be set, return <code>null</code>.
     * <p>
     * The default implementation unconditionally returns the value configured by the <strong>encoding </strong>
     * initialization parameter for this filter.
     * 
     * @param request The servlet request we are processing
     */
    protected String selectEncoding(ServletRequest request) {
        request.getLocale();
        return (this.encoding);
    }
}