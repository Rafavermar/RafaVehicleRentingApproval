package com.babel.vehiclerentingapproval.services.impl;

import com.babel.vehiclerentingapproval.exceptions.*;
import com.babel.vehiclerentingapproval.models.SolicitudRenting;
import com.babel.vehiclerentingapproval.models.TipoResultadoSolicitud;
import com.babel.vehiclerentingapproval.persistance.database.mappers.SolicitudRentingMapper;
import com.babel.vehiclerentingapproval.persistance.database.mappers.TipoResultadoSolicitudMapper;
import com.babel.vehiclerentingapproval.services.CodigoResolucionValidator;
import com.babel.vehiclerentingapproval.services.PersonaService;
import com.babel.vehiclerentingapproval.services.SolicitudRentingService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class SolicitudRentingServiceImpl implements SolicitudRentingService {
    private final SolicitudRentingMapper solicitudRentingMapper;
    private final TipoResultadoSolicitudMapper tipoResultadoSolicitudMapper;
    private final PersonaService personaService;
    private final CodigoResolucionValidator codigoResolucionValidator;

    public SolicitudRentingServiceImpl (SolicitudRentingMapper solicitudRentingMapper, TipoResultadoSolicitudMapper tipoResultadoSolicitudMapper, PersonaService personaService, CodigoResolucionValidator codigoResolucionValidator) {
        this.solicitudRentingMapper = solicitudRentingMapper;
        this.tipoResultadoSolicitudMapper = tipoResultadoSolicitudMapper;
        this.personaService = personaService;
        this.codigoResolucionValidator = codigoResolucionValidator;

    }

    /**
     * Agrega una nueva solicitud de renting, realizando varias validaciones antes de insertar la solicitud en la base de datos.
     *
     * @param solicitudRenting La solicitud de renting a agregar.
     * @return La solicitud de renting agregada, incluyendo la información de la persona asociada.
     * @throws RequestApiValidationException Si alguna de las validaciones no se cumple.
     * @see #validatePersona(int)
     * @see #validateNumVehiculos(SolicitudRenting)
     * @see #validateInversion(SolicitudRenting)
     * @see #validateCuota(SolicitudRenting)
     * @see #validatePlazo(SolicitudRenting)
     * @see #validateFecha(SolicitudRenting)
     */
    @Override
    public SolicitudRenting addSolicitudRenting (SolicitudRenting solicitudRenting) throws RequestApiValidationException {
        validatePersona(solicitudRenting.getPersona().getPersonaId());
        validateNumVehiculos(solicitudRenting);
        validateInversion(solicitudRenting);
        validateCuota(solicitudRenting);
        validatePlazo(solicitudRenting);
        validateFecha(solicitudRenting);
        solicitudRentingMapper.insertSolicitudRenting(solicitudRenting);
        solicitudRenting.setPersona(personaService.getPerson(solicitudRenting.getPersona().getPersonaId()));
        return solicitudRenting;
    }

    /**
     * Comprueba si existe una persona con el identificador proporcionado.
     *
     * @param idPersona el identificador de la persona a verificar
     * @throws PersonaNotFoundException si no se encuentra una persona con el identificador proporcionado
     */
    private void existIdPersona (int idPersona) throws PersonaNotFoundException {
        if (!personaService.existePersona(idPersona)) {
            throw new PersonaNotFoundException(idPersona);
        }
    }

    @Override
    public String verEstadoSolicitud (int idSolicitud) throws EstadoSolicitudNotFoundException, EstadoSolicitudInvalidException {
        int codigoExiste = tipoResultadoSolicitudMapper.existeCodigoResolucion(idSolicitud);

        if (codigoExiste == 0) { //idSolicitud or codResolucion null
            throw new EstadoSolicitudNotFoundException();
        }

        TipoResultadoSolicitud resultadoSolicitud = this.tipoResultadoSolicitudMapper.getResultadoSolicitud(idSolicitud);
        this.validarCodigoResolucion(resultadoSolicitud.getCodResultado());

        return resultadoSolicitud.getDescripcion();


    }

    private void validarCodigoResolucion (String CodResolucion) throws EstadoSolicitudInvalidException {
        this.codigoResolucionValidator.validarCodResolucion(CodResolucion);

    }

    public SolicitudRenting getSolicitudById (int id) throws SolicitudRentingNotFoundException {
        int existe = this.solicitudRentingMapper.existeSolicitud(id);
        SolicitudRenting solicitudRenting;
        if (existe == 1) {
            solicitudRenting = this.solicitudRentingMapper.getSolicitudByID(id);
        } else {
            throw new SolicitudRentingNotFoundException();
        }

        return solicitudRenting;
    }

    @Override
    public void modificaEstadoSolicitud (Integer solicitudId, TipoResultadoSolicitud nuevoEstado) throws SolicitudRentingNotFoundException, EstadoSolicitudNotFoundException {

        List<String> posiblesEstados = this.tipoResultadoSolicitudMapper.getListaEstados();
        int existeEstado = this.solicitudRentingMapper.existeSolicitud(solicitudId);

        if (!posiblesEstados.contains(nuevoEstado.getCodResultado())) {
            throw new EstadoSolicitudNotFoundException();
        }
        if (existeEstado == 0) {
            throw new SolicitudRentingNotFoundException();
        }

        this.solicitudRentingMapper.modificaEstadoSolicitud(solicitudId, nuevoEstado);
        System.out.println("\n\nCambios en tu solicitud.\nSu solicitud se encuentra: " + this.tipoResultadoSolicitudMapper.getEstadoSolicitud(solicitudId));

    }

    @Override
    public List<String> getListaEstados ( ) {
        List<String> listaEstados = this.tipoResultadoSolicitudMapper.getListaEstados();
        return listaEstados;
    }

    /**
     * Calcula la cantidad de dígitos en un objeto BigInteger.
     *
     * @param number el objeto BigInteger cuya cantidad de dígitos se desea calcular
     * @return la cantidad de dígitos en el objeto BigInteger; si el objeto BigInteger es nulo, devuelve 0
     */
    private int lenghtNumber (BigInteger number) {
        if (number != null) {
            String numeroString = number.toString();
            return numeroString.length();
        }
        return 0;
    }

    /**
     * Valida si existe una persona con el ID proporcionado.
     *
     * @param idPersona El identificador de la persona a validar.
     * @throws PersonaNotFoundException Si no se encuentra una persona con el ID especificado.
     * @see #existIdPersona(int)
     */
    private void validatePersona (int idPersona) throws PersonaNotFoundException {
        existIdPersona(idPersona);
    }

    /**
     * Valida el número de vehículos en una solicitud de renting.
     * Verifica si el valor de 'numVehiculos' cumple con las siguientes condiciones:
     * 1. No tiene más de 38 dígitos.
     * 2. No es nulo o vacío.
     * 3. No es negativo o igual a cero.
     *
     * @param solicitudRenting el objeto SolicitudRenting cuyo campo 'numVehiculos' se va a validar
     * @throws WrongLenghtFieldException      si el valor de 'numVehiculos' tiene más de 38 dígitos
     * @throws InputIsNullOrIsEmpty           si el valor de 'numVehiculos' es nulo o vacío
     * @throws InputIsNegativeOrZeroException si el valor de 'numVehiculos' es negativo o igual a cero
     * @see #lenghtNumber(BigInteger)
     */
    private void validateNumVehiculos (SolicitudRenting solicitudRenting) throws WrongLenghtFieldException, InputIsNullOrIsEmpty, InputIsNegativeOrZeroException {
        if (lenghtNumber(solicitudRenting.getNumVehiculos()) > 38) {
            throw new WrongLenghtFieldException("numVehiculos");
        }
        if (solicitudRenting.getNumVehiculos() == null) {
            throw new InputIsNullOrIsEmpty("numVehivulos");
        }
        if (solicitudRenting.getNumVehiculos().signum() == -1 || solicitudRenting.getNumVehiculos().signum() == 0) {
            throw new InputIsNegativeOrZeroException("numVehiculos");
        }
    }

    /**
     * Valida el campo 'inversion' en una solicitud de renting.
     * Verifica si el valor de 'inversion' cumple con las siguientes condiciones:
     * 1. No es nulo o vacío.
     * 2. No es negativo o igual a cero.
     *
     * @param solicitudRenting el objeto SolicitudRenting cuyo campo 'inversion' se va a validar
     * @throws InputIsNullOrIsEmpty           si el valor de 'inversion' es nulo o vacío
     * @throws InputIsNegativeOrZeroException si el valor de 'inversion' es negativo o igual a cero
     */
    private void validateInversion (SolicitudRenting solicitudRenting) throws InputIsNullOrIsEmpty, InputIsNegativeOrZeroException {
        if (solicitudRenting.getInversion() == null) {
            throw new InputIsNullOrIsEmpty("inversion");
        }
        if (solicitudRenting.getInversion() < 1) {
            throw new InputIsNegativeOrZeroException("inversion");
        }
    }

    /**
     * Valida el campo 'cuota' en una solicitud de renting.
     * Verifica si el valor de 'cuota' cumple con las siguientes condiciones:
     * 1. No es nulo o vacío.
     * 2. No es negativo o igual a cero.
     *
     * @param solicitudRenting el objeto SolicitudRenting cuyo campo 'cuota' se va a validar
     * @throws InputIsNullOrIsEmpty           si el valor de 'cuota' es nulo o vacío
     * @throws InputIsNegativeOrZeroException si el valor de 'cuota' es negativo o igual a cero
     */
    private void validateCuota (SolicitudRenting solicitudRenting) throws InputIsNullOrIsEmpty, InputIsNegativeOrZeroException {
        if (solicitudRenting.getCuota() == null) {
            throw new InputIsNullOrIsEmpty("cuota");
        }
        if (solicitudRenting.getCuota() < 1) {
            throw new InputIsNegativeOrZeroException("cuota");
        }
    }

    /**
     * Valida el campo 'plazo' en una solicitud de renting.
     * Verifica si el valor de 'plazo' cumple con las siguientes condiciones:
     * 1. No tiene más de 38 dígitos.
     * 2. No es negativo o igual a cero.
     * <p>
     * Si el valor de 'plazo' es nulo, no se lanza ninguna excepción.
     *
     * @param solicitudRenting el objeto SolicitudRenting cuyo campo 'plazo' se va a validar
     * @throws WrongLenghtFieldException      si el valor de 'plazo' tiene más de 38 dígitos
     * @throws InputIsNegativeOrZeroException si el valor de 'plazo' es negativo o igual a cero
     */
    private void validatePlazo (SolicitudRenting solicitudRenting) throws WrongLenghtFieldException, InputIsNegativeOrZeroException, InputIsNullOrIsEmpty {
        if (solicitudRenting.getPlazo() != null) {
            if (lenghtNumber(solicitudRenting.getPlazo()) > 38) {
                throw new WrongLenghtFieldException("Plazo");
            }
            if (solicitudRenting.getPlazo().signum() == -1 || solicitudRenting.getPlazo().signum() == 0) {
                throw new InputIsNegativeOrZeroException("plazo");
            }
        }
    }

    /**
     * Valida las fechas 'fechaInicioVigor' y 'fechaResolucion' en una solicitud de renting.
     * Verifica si la 'fechaInicioVigor' es posterior a la 'fechaResolucion'.
     * <p>
     * Si alguna de las fechas es nula, no se lanza ninguna excepción.
     *
     * @param solicitudRenting el objeto SolicitudRenting cuyas fechas se van a validar
     * @throws DateIsBeforeException si 'fechaInicioVigor' es anterior a 'fechaResolucion'
     */
    private void validateFecha (SolicitudRenting solicitudRenting) throws DateIsBeforeException {
        if (solicitudRenting.getFechaInicioVigor() != null && solicitudRenting.getFechaResolucion() != null) {
            if (solicitudRenting.getFechaInicioVigor().before(solicitudRenting.getFechaResolucion())) {
                throw new DateIsBeforeException("fechaInicioVigo", "fechaResolucion");
            }
        }
    }

    public void cancelarSolicitud (int id) throws SolicitudRentingNotFoundException {
        SolicitudRenting solicitudRenting = this.solicitudRentingMapper.getSolicitudByID(id);
        if (solicitudRenting == null) {
            throw new SolicitudRentingNotFoundException();
        }
        solicitudRentingMapper.cancelarSolicitud(solicitudRenting);
    }

}
