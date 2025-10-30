package net.risesoft.web.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import net.risesoft.config.ConfigConstants;
import net.risesoft.config.WatermarkConfigConstants;
import net.risesoft.utils.KkFileUtils;

public class AttributeSetFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // 空实现 - 该过滤器不需要在初始化时执行特定逻辑
        // 所有属性设置都在 doFilter 方法中动态处理
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
        throws IOException, ServletException {
        this.setWatermarkAttribute(request);
        this.setFileAttribute(request);
        filterChain.doFilter(request, response);
    }

    /**
     * 设置办公文具预览逻辑需要的属性
     * 
     * @param request request
     */
    private void setFileAttribute(ServletRequest request) {
        request.setAttribute("pdfPresentationModeDisable", ConfigConstants.getPdfPresentationModeDisable());
        request.setAttribute("pdfOpenFileDisable", ConfigConstants.getPdfOpenFileDisable());
        request.setAttribute("pdfPrintDisable", ConfigConstants.getPdfPrintDisable());
        request.setAttribute("pdfDownloadDisable", ConfigConstants.getPdfDownloadDisable());
        request.setAttribute("pdfBookmarkDisable", ConfigConstants.getPdfBookmarkDisable());
        request.setAttribute("pdfDisableEditing", ConfigConstants.getPdfDisableEditing());
        request.setAttribute("switchDisabled", ConfigConstants.getOfficePreviewSwitchDisabled());
        request.setAttribute("fileUploadDisable", ConfigConstants.getFileUploadDisable());
        request.setAttribute("beian", ConfigConstants.getBeian());
        request.setAttribute("size", ConfigConstants.maxSize());
        request.setAttribute("deleteCaptcha", ConfigConstants.getDeleteCaptcha());
        request.setAttribute("homePageNumber", ConfigConstants.getHomePageNumber());
        request.setAttribute("homePagination", ConfigConstants.getHomePagination());
        request.setAttribute("homePageSize", ConfigConstants.getHomePageSize());
        request.setAttribute("homeSearch", ConfigConstants.getHomeSearch());
    }

    /**
     * 设置水印属性
     * 
     * @param request request
     */

    private void setWatermarkAttribute(ServletRequest request) {
        // 设置水印文本
        setWatermarkParameter(request, "watermarkTxt", WatermarkConfigConstants.getWatermarkTxt());

        // 设置数值型水印参数
        setNumericWatermarkParameter(request, "watermarkXSpace", WatermarkConfigConstants.getWatermarkXSpace());
        setNumericWatermarkParameter(request, "watermarkYSpace", WatermarkConfigConstants.getWatermarkYSpace());
        setNumericWatermarkParameter(request, "watermarkAlpha", WatermarkConfigConstants.getWatermarkAlpha());
        setNumericWatermarkParameter(request, "watermarkWidth", WatermarkConfigConstants.getWatermarkWidth());
        setNumericWatermarkParameter(request, "watermarkHeight", WatermarkConfigConstants.getWatermarkHeight());
        setNumericWatermarkParameter(request, "watermarkAngle", WatermarkConfigConstants.getWatermarkAngle());

        // 设置其他水印参数
        setWatermarkParameter(request, "watermarkFont", WatermarkConfigConstants.getWatermarkFont());
        setWatermarkParameter(request, "watermarkFontsize", WatermarkConfigConstants.getWatermarkFontsize());
        setWatermarkParameter(request, "watermarkColor", WatermarkConfigConstants.getWatermarkColor());
    }

    /**
     * 设置水印参数（通用方法）
     *
     * @param request Servlet请求
     * @param paramName 参数名
     * @param defaultValue 默认值
     */
    private void setWatermarkParameter(ServletRequest request, String paramName, String defaultValue) {
        String paramValue = KkFileUtils.htmlEscape(request.getParameter(paramName));
        request.setAttribute(paramName, paramValue != null ? paramValue : defaultValue);
    }

    /**
     * 设置数值型水印参数
     *
     * @param request Servlet请求
     * @param paramName 参数名
     * @param defaultValue 默认值
     */
    private void setNumericWatermarkParameter(ServletRequest request, String paramName, String defaultValue) {
        String paramValue = KkFileUtils.htmlEscape(request.getParameter(paramName));
        if (!KkFileUtils.isInteger(paramValue)) {
            paramValue = null;
        }
        request.setAttribute(paramName, paramValue != null ? paramValue : defaultValue);
    }

    @Override
    public void destroy() {
        // 空实现 - 该过滤器不需要在销毁时执行特定清理逻辑
    }
}
