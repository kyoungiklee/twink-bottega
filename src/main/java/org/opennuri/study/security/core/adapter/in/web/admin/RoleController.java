package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.opennuri.study.security.core.application.port.in.RoleManageCommand;
import org.opennuri.study.security.core.application.port.in.RoleManageUseCase;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RoleController {
    private final RoleManageUseCase roleManageUseCase;

    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/admin/roles")
    public String getRoles(Model model) {

        Optional<List<Role>> optionalRoles = roleManageUseCase.findAll();
        optionalRoles.ifPresentOrElse(roles ->
                        model.addAttribute("roleList",
                                roles.stream().map(
                                        role -> modelMapper.map(role, RoleDto.class)
                                ).collect(Collectors.toList()))
                , () -> model.addAttribute(new ArrayList<Role>()));
        return "admin/role/list";
    }

    @GetMapping("/admin/roles/register")
    public String registerForm(Model model) {

        model.addAttribute("roleDto", new RoleDto());

        return "admin/role/detail";
    }

    @PostMapping("/admin/roles/register")
    public String register(@Valid RoleDto roleDto, BindingResult result) {

        log.info("RoleDto : {}", roleDto);

        if (result.hasErrors()) {
            log.info("{} : has errors {}", result.getClass().getName(), result.getErrorCount());
            return "admin/role/detail";
        }

        RoleManageCommand command = RoleManageCommand.builder()
                .id(roleDto.getId())
                .roleName(roleDto.getRoleName())
                .description(roleDto.getDescription())
                .build();

        Role register = roleManageUseCase.register(command);
        log.info("register : {}", register);

        return "redirect:/admin/roles";
    }

    @GetMapping("/admin/roles/{id}/edit")
    public String getRole(@PathVariable("id") @PositiveOrZero Long id, Model model) {

        Optional<Role> optionalRole = roleManageUseCase.findById(id);

        optionalRole.ifPresentOrElse(role ->
                        model.addAttribute("roleDto", modelMapper.map(role, RoleDto.class))
                , () -> model.addAttribute("roleDto", new RoleDto()));

        return "admin/role/detail";
    }

    @GetMapping("/admin/roles/{id}/delete")
    public String removeRole(@PathVariable("id") Long id) {

        boolean isSuccess = roleManageUseCase.delete(id);
        log.info("delete success: {}", isSuccess);

        if (!isSuccess) {
            throw new IllegalArgumentException(String.format("삭제에 실패하였습니다: %s", id));
        }
        return "redirect:/admin/roles";
    }
}
