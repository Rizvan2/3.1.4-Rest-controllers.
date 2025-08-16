package ru.kata.spring.boot_security.demo.controller;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.mapper.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает полный список пользователей в системе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список пользователей успешно получен",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователи не найдены (пустой список)",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<List<UserDto>> getListAllUsers() {
        List<User> users = userService.listAllUsers();
        List<UserDto> dtos = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/getUserById")
    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает данные пользователя по его идентификатору",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID пользователя",
                            required = true,
                            example = "1"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = UserDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<UserDto> getUserById(@RequestParam("id") int id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(UserMapper.toDto(user), HttpStatus.OK);
    }

    @GetMapping("getCurrentUser")
    @Operation(
            summary = "Получить данные текущего аутентифицированного пользователя",
            description = "Возвращает информацию о пользователе, выполнившем запрос, на основе данных аутентификации",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя успешно получены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Пользователь не аутентифицирован",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Доступ запрещен",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<UserDto> getCurrentUserId(Principal principal) {
        UserDto userDto = UserMapper.toDto(userService.findUserByUsername(principal.getName()));
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/saveUser")
    @Operation(
            summary = "Создать нового пользователя",
            description = "Сохраняет нового пользователя в системе",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные пользователя для создания",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Пример запроса",
                                            value = "{\n" +
                                                    "  \"username\": \"newuser\",\n" +
                                                    "  \"password\": \"password123\",\n" +
                                                    "  \"roles\": [\"ROLE_USER\"]\n" +
                                                    "}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Пользователь успешно создан",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные пользователя",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Пользователь с таким именем уже существует",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto, roleService.findRolesByNameIn(userDto.getRoles()));
        userService.save(user);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @PostMapping("/update")
    @Operation(
            summary = "Обновить данные пользователя",
            description = "Обновляет информацию о существующем пользователе в системе, если пароль не указан оставляет старый, по умолчанию пароль скрыт",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Обновленные данные пользователя",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Пример запроса",
                                            value = "{\n" +
                                                    "  \"id\": 1,\n" +
                                                    "  \"username\": \"updatedUser\",\n" +
                                                    "  \"password\": \"+-newPassword123\",\n" +
                                                    "  \"roles\": [\"ADMIN\"]\n" +
                                                    "}"
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Данные пользователя успешно обновлены",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректные данные пользователя",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Конфликт данных (например, username уже используется)",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        User user = UserMapper.toEntity(userDto, roleService.findRolesByNameIn(userDto.getRoles()));
        userService.update(user);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/delete")
    @Operation(
            summary = "Удалить пользователя по ID",
            description = "Удаляет пользователя с указанным идентификатором из системы",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "ID пользователя для удаления",
                            required = true,
                            example = "1",
                            schema = @Schema(type = "integer", format = "int32")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Пользователь успешно удален",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный ID пользователя",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь с указанным ID не найден",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<Void> deleteUserById(@RequestParam("id") int id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}