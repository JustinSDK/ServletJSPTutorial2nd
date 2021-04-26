package cc.openhome.hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class HelloApplication {
    @GetMapping("user")
    @ResponseBody
    public String user(String name) {
        return String.format("哈囉！%s！", name);
    }

    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
