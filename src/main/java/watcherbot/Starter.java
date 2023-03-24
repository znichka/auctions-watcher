package watcherbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

//@Configuration
//@ComponentScan
@SpringBootApplication
public class Starter {
    public static void main(String[] args)  {
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
//        context.register(Starter.class);
//        context.refresh();

        SpringApplication.run(Starter.class, args);

    }
}
