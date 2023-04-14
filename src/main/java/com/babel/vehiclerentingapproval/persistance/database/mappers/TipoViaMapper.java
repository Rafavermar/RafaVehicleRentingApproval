package com.babel.vehiclerentingapproval.persistance.database.mappers;

import com.babel.vehiclerentingapproval.models.Direccion;
import com.babel.vehiclerentingapproval.models.TipoVia;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
/**
 * Esta clase proporciona metodos de manejo de TipoVia en la base de datos
 *
 * @author enrique.munoz@babelgroup.com,andres.guijarro@babelgroup.com
 */
@Mapper
public interface TipoViaMapper {
    /**
     * Consulta que devuelve inserta un tipo de via en la base de datos
     *
     * @param tipoVia Objeto TipoVia a insertar
     * @see TipoVia
     */
    @Insert("INSERT INTO TIPO_VIA (TIPO_VIA_ID, DESCRIPCION) VALUES (#{tipoViaId}, #{descripcion})")
    @Options(useGeneratedKeys = true, keyProperty = "tipoViaId", keyColumn = "TIPO_VIA_ID")
    void insertTipoVia(TipoVia tipoVia);
    /**
     * Consulta que devuelve un tipo de via dada su ID
     *
     * @param tipoViaId ID del tipo de via que se quiere obtener
     * @see TipoVia
     * @return Objeto TipoVia
     */
    @Select("SELECT TIPO_VIA_ID, DESCRIPCION FROM TIPO_VIA WHERE TIPO_VIA_ID = #{tipoViaId}")
    TipoVia getTipoVia(int tipoViaId);
}
