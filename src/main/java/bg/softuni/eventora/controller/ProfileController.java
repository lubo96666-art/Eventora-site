package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.ProfileDto;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.service.ProfileService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class ProfileController {
    private final ProfileService service;

    public ProfileController(ProfileService service) { this.service = service; }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        User user = service.byId((UUID) session.getAttribute("userId"));
        model.addAttribute("user", user);
        model.addAttribute("profileDto", service.toDto(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String update(@Valid @ModelAttribute ProfileDto profileDto, BindingResult result, Model model, HttpSession session) {
        UUID id = (UUID) session.getAttribute("userId");
        if (result.hasErrors()) {
            model.addAttribute("user", service.byId(id));
            return "profile";
        }
        User updated = service.update(id, profileDto);
        session.setAttribute("username", updated.getUsername());
        return "redirect:/profile?updated=true";
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", service.allUsers());
        return "admin-users";
    }

    @PostMapping("/admin/users/{id}/role")
    public String role(@PathVariable UUID id, @RequestParam String role) {
        service.changeRole(id, role);
        return "redirect:/admin/users?updated=true";
    }
}
