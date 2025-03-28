package ru.kata.spring.boot_security.demo.dto;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public class UserUpdateRequest {
    private User user;
    private List<Long> selectedRoleIds;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Long> getSelectedRoleIds() {
        return selectedRoleIds;
    }

    public void setSelectedRoleIds(List<Long> selectedRoleIds) {
        this.selectedRoleIds = selectedRoleIds;
    }
}
