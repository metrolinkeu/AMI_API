package com.metrolink.ami_api.services.procesos.programacionesAmi;

import org.springframework.stereotype.Service;

@Service
public class ConectorProgramacionService {

    public String UsarConectorProgramacion (String mensaje){

        System.out.println(mensaje);

        return "Se imprimio el mensaje";

    }


    
}
