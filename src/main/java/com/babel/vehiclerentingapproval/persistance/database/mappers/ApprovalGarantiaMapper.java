package com.babel.vehiclerentingapproval.persistance.database.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ApprovalGarantiaMapper {
    @Select("Select COUNT(SOLICITUD_ID) from SCORING.SOLICITUD_RENTING where PERSONA_ID = #{personaId} AND COD_RESOLUCION NOT LIKE 'AG%' AND FECHA_RESOLUCION <= ADD_MONTHS(SYSDATE,-24);")
    int existeClienteAprobadoConGarantias(int personaId);
}