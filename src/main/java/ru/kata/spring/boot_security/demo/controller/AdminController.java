package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserUpdateRequest;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getListAllUsers() {
        List<User> users = userService.listAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam("id") int id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("getCurrentUser")
    public ResponseEntity<User> getCurrentUserId(Principal principal) {
        User user = userService.findUserByUsername(principal.getName());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/saveUser")
    public ResponseEntity<User> addUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userUpdateRequest.getUser();
        user.setRoles(roleService.getRolesFromIds(userUpdateRequest.getSelectedRoleIds()));
        userService.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        User user = userUpdateRequest.getUser();
        user.setRoles(roleService.getRolesFromIds(userUpdateRequest.getSelectedRoleIds()));
        userService.update(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteUserById(@RequestParam("id") int id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}