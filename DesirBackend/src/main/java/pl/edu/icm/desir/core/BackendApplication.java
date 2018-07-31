package pl.edu.icm.desir.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    
}


/*
Once run backend application might be tested in the browser by using the following link:

http://localhost:8080/dataobject?id=Test1&text=

*/