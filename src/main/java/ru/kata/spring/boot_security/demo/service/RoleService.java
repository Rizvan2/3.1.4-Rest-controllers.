package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    Optional<Role> findRoleByName(String roleName);

    Role findRoleById(long id);

    Set<Role> getRolesFromIds(List<Long> selectedRoleIds);

    List<Role> findAllRoles();

}
