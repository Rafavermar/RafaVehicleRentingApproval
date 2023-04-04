package com.babel.vehiclerentingapproval.services.preautomaticresults.impl;

import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.services.preautomaticresults.DenyRulesService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;
@Service
public class DenyRulesServicesImpl implements DenyRulesService {

    private static final LocalDate fechaActual = LocalDate.now();
    private static final int anyosMayor = 18;
    private static final int scoringRating = 6;
    private static final int anyosPlazo = 80;


    @Override
    public Boolean validateClientAge(SolicitudRenting solicitudRenting) {
        int anyo = solicitudRenting.getPersona().getFechaNacimiento().getYear();
        int day = solicitudRenting.getPersona().getFechaNacimiento().getDay();
        int month = solicitudRenting.getPersona().getFechaNacimiento().getMonth();
        LocalDate fechaConcreta = LocalDate.of(anyo, month, day);
        long anios = ChronoUnit.YEARS.between(fechaConcreta, fechaActual);
        if(anios<anyosMayor){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Boolean validateScoringTitular(SolicitudRenting solicitudRenting) {
        if (solicitudRenting.getPersona().getScoring() >= scoringRating) {
            return true;

        } else {
            return false;
        }
    }

    @Override
    public Boolean validateClientAgePlusPlazo(SolicitudRenting solicitudRenting) {
        int anyo = solicitudRenting.getPersona().getFechaNacimiento().getYear();
        int day = solicitudRenting.getPersona().getFechaNacimiento().getDay();
        int month = solicitudRenting.getPersona().getFechaNacimiento().getMonth();
        LocalDate fechaConcreta = LocalDate.of(anyo, month, day);
        long anios = ChronoUnit.YEARS.between(fechaConcreta, fechaActual);
        if(anios + solicitudRenting.getPlazo()>=anyosPlazo){
            return true;
        }else{
            return false;
        }
    }

}
