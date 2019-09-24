package pl.edu.icm.desir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan({"pl.edu.icm.desir"})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    
}


/*
Once run backend application might be tested in the browser by using the following link:

http://localhost:8080/dataobject?id=Test1&text=

*/