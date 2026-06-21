package bg.softuni.eventora.controller;

import bg.softuni.eventora.dto.*;
import bg.softuni.eventora.model.User;
import bg.softuni.eventora.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final AuthService authService;
    private final PageController pageController;

    public AuthController(AuthService authService, PageController pageController) {
        this.authService = authService;
        this.pageController = pageController;
    }

    @GetMapping("/register")
    public String register(Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "register");
        return "visualization";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDto registerDto, BindingResult br, Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "register");
        if (br.hasErrors()) return "visualization";
        try {
            String email = authService.register(registerDto);
            VerifyCodeDto verifyCodeDto = new VerifyCodeDto();
            verifyCodeDto.setEmail(email);
            model.addAttribute("verifyCodeDto", verifyCodeDto);
            model.addAttribute("authMode", "verifyCode");
            model.addAttribute("message", "Изпратихме 6-цифрен код на " + email + ". Въведи кода, за да активираш профила.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "visualization";
    }

    @PostMapping("/verify-code")
    public String verifyCode(@Valid @ModelAttribute VerifyCodeDto verifyCodeDto, BindingResult br, Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "verifyCode");
        model.addAttribute("verifyCodeDto", verifyCodeDto);
        if (br.hasErrors()) return "visualization";
        try {
            User user = authService.verifyCode(verifyCodeDto);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole());
            return "redirect:/visualization";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "visualization";
    }

    @PostMapping("/resend-code")
    public String resendCode(@RequestParam String email, Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "verifyCode");
        VerifyCodeDto verifyCodeDto = new VerifyCodeDto();
        verifyCodeDto.setEmail(email);
        model.addAttribute("verifyCodeDto", verifyCodeDto);
        try {
            authService.resendVerificationCode(email);
            model.addAttribute("message", "Изпратихме нов код на " + email + ".");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "visualization";
    }

    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "login");
        return "visualization";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDto loginDto, BindingResult br, HttpSession session, Model model) {
        pageController.addVisualizationModel(model, session, "login");
        if (br.hasErrors()) return "visualization";
        try {
            authService.login(loginDto, session);
            return "redirect:/visualization";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "visualization";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/visualization";
    }

    @GetMapping("/forgot-password")
    public String forgot(Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "forgot");
        return "visualization";
    }

    @PostMapping("/forgot-password")
    public String forgot(@Valid @ModelAttribute ForgotPasswordDto forgotPasswordDto, BindingResult br, Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "forgot");
        if (br.hasErrors()) return "visualization";
        authService.sendPasswordReset(forgotPasswordDto);
        model.addAttribute("authMode", "message");
        model.addAttribute("message", "Ако има профил с този имейл, ще получиш линк за смяна на парола.");
        return "visualization";
    }

    @GetMapping("/reset-password")
    public String reset(@RequestParam String token, Model model, HttpSession session) {
        ResetPasswordDto dto = new ResetPasswordDto();
        dto.setToken(token);
        model.addAttribute("resetPasswordDto", dto);
        pageController.addVisualizationModel(model, session, "reset");
        return "visualization";
    }

    @PostMapping("/reset-password")
    public String reset(@Valid @ModelAttribute ResetPasswordDto resetPasswordDto, BindingResult br, Model model, HttpSession session) {
        pageController.addVisualizationModel(model, session, "reset");
        if (br.hasErrors()) return "visualization";
        try {
            authService.resetPassword(resetPasswordDto);
            model.addAttribute("authMode", "message");
            model.addAttribute("message", "Паролата е сменена успешно.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "visualization";
    }
}
