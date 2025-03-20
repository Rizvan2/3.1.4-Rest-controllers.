package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleServiceImp implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImp(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRoleByName(String roleName) {
        return roleRepository.findRoleByName(roleName);
    }

    @Override
    @Transactional(readOnly = true)
    public Role findRoleById(long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Role> getRolesFromIds(List<Long> selectedRoleIds) {
        return selectedRoleIds != null ? selectedRoleIds
                .stream().map(roleRepository::findRoleById)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()) : new HashSet<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

}
