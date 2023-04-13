package com.babel.vehiclerentingapproval.services;

import com.babel.vehiclerentingapproval.exceptions.EstadoSolicitudInvalidException;
import com.babel.vehiclerentingapproval.exceptions.EstadoSolicitudNotFoundException;
import com.babel.vehiclerentingapproval.exceptions.RequestApiValidationException;
import com.babel.vehiclerentingapproval.exceptions.SolicitudRentingNotFoundException;
import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.models.TipoResultadoSolicitud;

import java.util.List;

public interface SolicitudRentingService {
    SolicitudRenting addSolicitudRenting (SolicitudRenting solicitudRenting) throws RequestApiValidationException;

    /**
     * Método que devuelve el estado de una solicitud
     * @param idSolicitud ID de la solicitud de renting
     * @return String con el estado de la solicitud
     * @throws RequestApiValidationException si la id de la solicitud no existe, el codigo de resolucion es nulo, o no es valido
     * @see RequestApiValidationException
     */
    public String verEstadoSolicitud (int idSolicitud) throws RequestApiValidationException;

    public SolicitudRenting getSolicitudById (int it);

    void modificaEstadoSolicitud (Integer solicitudId, TipoResultadoSolicitud nuevoEstado) throws EstadoSolicitudNotFoundException, SolicitudRentingNotFoundException;

    public List<String> getListaEstados ( );

    void cancelarSolicitud (int id);
}
