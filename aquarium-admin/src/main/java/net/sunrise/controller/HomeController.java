package net.sunrise.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created on January, 2018
 *
 * @author bqduc
 */
@Controller
public class HomeController {

	@GetMapping("")
	public String getHome(){
		return "pages/public/dashboard/master";//"index";
	}
}
