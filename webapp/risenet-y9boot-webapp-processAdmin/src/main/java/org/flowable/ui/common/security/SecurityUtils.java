/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.flowable.ui.common.security;

import org.flowable.common.engine.api.FlowableIllegalStateException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import net.risesoft.model.user.UserInfo;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * Utility class for Spring Security.
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class SecurityUtils {

    static final String GROUP_PREFIX = "GROUP_";
    static final String TENANT_PREFIX = "TENANT_";

    private static SecurityScopeProvider securityScopeProvider = new FlowableSecurityScopeProvider();

    public static GrantedAuthority createGroupAuthority(String groupId) {
        return new SimpleGrantedAuthority(GROUP_PREFIX + groupId);
    }

    public static GrantedAuthority createTenantAuthority(String tenantId) {
        return new SimpleGrantedAuthority(TENANT_PREFIX + tenantId);
    }

    public static SecurityScope getAuthenticatedSecurityScope() {
        SecurityScope currentSecurityScope = getCurrentSecurityScope();
        if (currentSecurityScope != null) {
            return currentSecurityScope;
        }
        throw new FlowableIllegalStateException("User is not authenticated");
    }

    public static SecurityScope getCurrentSecurityScope() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null && securityContext.getAuthentication() != null) {
            return getSecurityScope(securityContext.getAuthentication());
        }
        return null;
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentUserId() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        if (userInfo != null) {
            return userInfo.getName();
        }
        return null;
    }

    public static SecurityScope getSecurityScope(Authentication authentication) {
        return securityScopeProvider.getSecurityScope(authentication);
    }

    public static void setSecurityScopeProvider(SecurityScopeProvider securityScopeProvider) {
        SecurityUtils.securityScopeProvider = securityScopeProvider;
    }

    private SecurityUtils() {}

}