package me.ucake.session.samples.springmvc.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by alexqdjay on 2017/8/20.
 */
@RestController
@RequestMapping("/samples")
public class SampleController {

    @RequestMapping("/echo")
    public void echo(@RequestParam String msg, HttpServletRequest request,
                       HttpServletResponse response) throws IOException, InterruptedException {
        HttpSession session = request.getSession(true);
        System.out.println(request.getClass());
        System.out.println(request.isRequestedSessionIdFromURL());
        System.out.println(request.isRequestedSessionIdFromCookie());
        System.out.println("Echo: ".concat(msg));
        int i = 100;
        while (true) {
            response.getWriter().println("just for testing");
            response.getWriter().flush();
            TimeUnit.MILLISECONDS.sleep(10);
            if (i-- == 0) {
                break;
            }
        }

        session.setAttribute("name", "alex");
//        throw new RuntimeException("121212");
        return;
    }

}
