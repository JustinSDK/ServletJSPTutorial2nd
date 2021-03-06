package cc.openhome.web;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableWebMvc
@PropertySource("classpath:web.properties")
@EnableAspectJAutoProxy 
@ComponentScan(basePackages = {"cc.openhome.controller", "cc.openhome.aspect"})
public class WebConfig implements WebMvcConfigurer, ApplicationContextAware {
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }
    
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
       return new PropertySourcesPlaceholderConfigurer();
    }  
    
    @Bean
    public PolicyFactory htmlPolicy() {
        return new HtmlPolicyBuilder()
                    .allowElements("a", "b", "i", "del", "pre", "code")
                    .allowUrlProtocols("http", "https")
                    .allowAttributes("href").onElements("a")
                    .requireRelNofollowOnLinks()
                    .toFactory();
    }

    private ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }    
    
    @Bean
    public ITemplateResolver templateResolver() {
        // ????????????????????????????????????????????????????????????????????????
        var resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(applicationContext);

        // ????????????????????????????????????????????????????????????????????????????????????
        resolver.setCacheable(false);
        // ???????????????????????????????????????
        resolver.setPrefix("/WEB-INF/templates/");
        // ???????????????????????????????????????
        resolver.setSuffix(".html");
        // HTML ????????????
        resolver.setCharacterEncoding("UTF-8");
        // ???????????? HTML ??????
        resolver.setTemplateMode(TemplateMode.HTML);
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(ITemplateResolver templateResolver) {
        // ???????????????????????????
        var engine = new SpringTemplateEngine();
        engine.setEnableSpringELCompiler(true);
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public ViewResolver viewResolver(SpringTemplateEngine engine) {
        // ??????ViewResolver???????????????????????????????????????
        var resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(engine);
        // ??????????????????
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCache(false);
        return resolver;
    }    
}