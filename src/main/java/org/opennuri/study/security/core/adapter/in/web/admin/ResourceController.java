package org.opennuri.study.security.core.adapter.in.web.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.opennuri.study.security.core.application.port.in.*;
import org.opennuri.study.security.core.domain.Resource;
import org.opennuri.study.security.core.domain.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
//todo: 사용자 입력값에 대한 검증과 검증결과 표시 구현
public class ResourceController {
    private final RoleManageUseCase roleManageUseCase;

    private final ResourceManageUseCase resourceManageUseCase;

    private final ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/admin/resources")
    public String getResources(Model model) {
        Optional<List<Resource>> optionalResourceList = resourceManageUseCase.findAll();
        optionalResourceList.ifPresentOrElse(resources ->
                        model.addAttribute("resources", resources.stream()
                                .map(resource -> modelMapper.map(resource, ResourceDto.class))
                                .collect(Collectors.toList()))
                , () -> model.addAttribute("resources", new ArrayList<ResourceDto>()));

        return "/admin/resource/list";
    }


    @GetMapping("/admin/resources/{id}")
    public String getResources(@PathVariable("id") Long id, Model model) {

        log.info("pathvariable id : {}", id);
        // 1. id에 해당하는 리소스를 찾는다.
        Optional<Resource> optionalResource = resourceManageUseCase.findById(id);

        // 2. resourceDto로 변환하여모델에 담고 view에 전달한다.
        // 값이 없는 경우 빈 resourceDto를 전달한다.

        // resource를 resourceDto로 변환하여 전달한다. 사용자로부터 전달 받은 값을 검증하기위해 resourceDto를 사용한다.
        // roles -> roleDto로 변환하여 전달
        optionalResource.ifPresentOrElse(resource ->
                        model.addAttribute("resourceDto", ResourceDto.builder()
                                .id(resource.getId())
                                .resourceName(resource.getResourceName())
                                .resourceType(resource.getResourceType())
                                .httpMethod(resource.getHttpMethod())
                                .orderNum(resource.getOrderNum())
                                .description(resource.getDescription())
                                .roles(resource.getRoleSet().stream()
                                        .map(Role::getRoleName)
                                        .collect(Collectors.toSet()))
                                .build())
                , () -> model.addAttribute("resourceDto", new ResourceDto())
        );

        // 3. Role을 선택할후 있도록 RoleDto 목록을 view에 전달한다.
        Optional<List<Role>> optionalRoles = roleManageUseCase.findAll();
        optionalRoles.ifPresentOrElse(roles ->
                        model.addAttribute("roles", roles.stream()
                                .map(role -> modelMapper.map(role, RoleDto.class))
                                .collect(Collectors.toList())
                        )
                , () -> model.addAttribute(List.of(new RoleDto())));

        return "admin/resource/detail";
    }

    @GetMapping("/admin/resources/register")
    public String getRegisterForm(Model model) {

        Optional<List<Role>> optionalRoles = roleManageUseCase.findAll();
        optionalRoles.ifPresentOrElse(roles ->
                        model.addAttribute("roles", roles.stream()
                                .map(role -> modelMapper.map(role, RoleDto.class))
                                .collect(Collectors.toSet())
                        )
                , () -> model.addAttribute("roles", Set.of(new RoleDto()))
        );
        model.addAttribute("resourceDto", new ResourceDto());
        return "admin/resource/detail";
    }

    @PostMapping("/admin/resources/register")
    public String register(@Valid ResourceDto resourceDto, BindingResult result, Model model) {

        if (result.hasErrors()) {
            Optional<List<Role>> optionalRoles = roleManageUseCase.findAll();
            optionalRoles.ifPresentOrElse(roles ->
                            model.addAttribute("roles", roles.stream()
                                    .map(role -> modelMapper.map(role, RoleDto.class))
                                    .collect(Collectors.toSet())
                            )
                    , () -> model.addAttribute("roles", Set.of(new RoleDto()))
            );
            log.info("validation error : {}", result.getErrorCount());
            return "admin/resource/detail";

        }

        Optional.ofNullable(resourceDto.getId()).ifPresentOrElse(
                id -> {
                    log.info("id : {}", id);
                    UpdateResourceCommand command = UpdateResourceCommand.builder()
                            .id(id)
                            .resourceName(resourceDto.getResourceName())
                            .resourceType(resourceDto.getResourceType())
                            .httpMethod(resourceDto.getHttpMethod())
                            .orderNum(resourceDto.getOrderNum())
                            .description(resourceDto.getDescription())
                            .roles(resourceDto.getRoles())
                            .build();
                    Resource resource = resourceManageUseCase.updateResource(command);
                },
                () -> {
                    RegisterResourceCommand command = RegisterResourceCommand.builder()
                            .resourceName(resourceDto.getResourceName())
                            .resourceType(resourceDto.getResourceType())
                            .httpMethod(resourceDto.getHttpMethod())
                            .orderNum(resourceDto.getOrderNum())
                            .description(resourceDto.getDescription())
                            .orderNum(resourceDto.getOrderNum())
                            .roles(resourceDto.getRoles())
                            .build();

                    Resource resource = resourceManageUseCase.createResource(command);
                    log.info("resource : {}", resource);
                }
        );

        return "redirect:/admin/resources";
    }

    @GetMapping("/admin/resources/delete/{id}")
    public String delete(@PathVariable("id") Long id) {

        log.info("pathvariable id: {}", id);
        boolean isSuccess = resourceManageUseCase.deleteResource(id);

        if (!isSuccess) {
            throw new IllegalStateException("요청한 작업을 실패하였습니다");
        }

        return "redirect:/admin/resources";
    }
}
