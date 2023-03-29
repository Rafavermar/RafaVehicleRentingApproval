package com.babel.vehiclerentingapproval.services;

import com.babel.vehiclerentingapproval.exceptions.EstadoSolicitudNotFoundException;
import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.models.TipoResultadoSolicitud;

public interface SolicitudRentingService {
    public void createRentingRequest(SolicitudRenting solicitudRenting);
    public void validateRentingRequest(SolicitudRenting solicitudRenting);

    public String verEstadoSolicitud(int idSolicitud) throws EstadoSolicitudNotFoundException;
    public SolicitudRenting getSolicitudById(int it);

    void modificaEstadoSolicitud(Integer solicitudId, TipoResultadoSolicitud nuevoEstado) throws EstadoSolicitudNotFoundException ;
}
