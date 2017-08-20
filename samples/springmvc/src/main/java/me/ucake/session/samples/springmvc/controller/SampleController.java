package me.ucake.session.samples.springmvc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by alexqdjay on 2017/8/20.
 */
@RestController
@RequestMapping("/samples")
public class SampleController {

    @RequestMapping("/echo")
    public String echo(@RequestParam String msg, HttpServletRequest request) {
        System.out.println(request.getClass());
        System.out.println("Echo: ".concat(msg));
        return msg;
    }

}
