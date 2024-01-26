package net.risesoft.model;

import org.flowable.idm.api.User;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class ProcessUser implements User {

    private static final long serialVersionUID = 4114086269836648630L;

    private String id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String tenantId;

    private String displayName;

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getTenantId() {
        return tenantId;
    }

    @Override
    public boolean isPictureSet() {
        return false;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
