package com.sj.oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@SpringBootApplication
public class Oauth2ServerApplication {

	@GetMapping("/user")
	public @ResponseBody Principal user(Principal user){
		return user;
	}

	/**
	 * @see org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter
	 * @param model
	 * @param request
	 * @param error
	 * @return
	 */
	@GetMapping("/login")
	public String login(Model model, HttpServletRequest request, @RequestParam(value = "error", required = false) String error) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (token != null) {
			model.addAttribute("tokenName",token.getParameterName());
			model.addAttribute("token",token.getToken());
		}
		if (error != null) {
			model.addAttribute("error", "用户名或密码错误");
		}
		return "login";
	}

	public static void main(String[] args) {
		SpringApplication.run(Oauth2ServerApplication.class, args);
	}
}
