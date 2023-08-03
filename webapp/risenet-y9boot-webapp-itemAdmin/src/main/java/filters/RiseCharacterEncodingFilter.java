package filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class RiseCharacterEncodingFilter extends OncePerRequestFilter {
    static final Pattern INPUTPATTERN = Pattern.compile(".*_input_encode=([\\w-]+).*");

    static final Pattern OUTPUTPATTERN = Pattern.compile(".*_output_encode=([\\w-]+).*");

    private String encoding;

    private boolean forceEncoding = false;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String url = request.getQueryString();
        Matcher m = null;
        if (url != null && (m = INPUTPATTERN.matcher(url)).matches()) {
            String inputEncoding = m.group(1);
            request.setCharacterEncoding(inputEncoding);
            m = OUTPUTPATTERN.matcher(url);
            if (m.matches()) {
                response.setCharacterEncoding(m.group(1));
            } else {
                if (this.forceEncoding) {
                    response.setCharacterEncoding(this.encoding);
                }
            }
        } else {
            boolean b = this.encoding != null && (this.forceEncoding || request.getCharacterEncoding() == null);
            if (b) {
                request.setCharacterEncoding(this.encoding);
                if (this.forceEncoding) {
                    response.setCharacterEncoding(this.encoding);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Set the encoding to use for requests. This encoding will be passed into a
     * {@link javax.servlet.http.HttpServletRequest#setCharacterEncoding} call.
     * <p>
     * Whether this encoding will override existing request encodings (and whether it will be applied as default
     * response encoding as well) depends on the {@link #setForceEncoding "forceEncoding"} flag.
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set whether the configured {@link #setEncoding encoding} of this filter is supposed to override existing request
     * and response encodings.
     * <p>
     * Default is "false", i.e. do not modify the encoding if
     * {@link javax.servlet.http.HttpServletRequest#getCharacterEncoding()} returns a non-null value. Switch this to
     * "true" to enforce the specified encoding in any case, applying it as default response encoding as well.
     * <p>
     * Note that the response encoding will only be set on Servlet 2.4+ containers, since Servlet 2.3 did not provide a
     * facility for setting a default response encoding.
     */
    public void setForceEncoding(boolean forceEncoding) {
        this.forceEncoding = forceEncoding;
    }

}
