/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.devs.ticketapp.dao;

import com.devs.ticketapp.dto.HeaderMsg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author azus
 */
public class TipoEmpresaModel extends CommonsDAO{
    
    
/* Query para obtener todos las colas de la tabla 
 * 
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */
    
     public String obtenerTotalTipoEmpresas() {
        String result="";
        String sql = "select\n" +
                "	json_agg(\n" +
                "    json_build_object(\n" +
                "        'idtipo', te.id_tipo_empresa ,\n" +
                "        'tipo', te.tipo_empresa, \n "+
                "        'descripcion', te.descripcion,\n" +
                "        'imagen', te.imagen  \n   " +
                "    )\n" +
                "    ) as resultado\n" +
                "FROM tipo_empresa te";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
             Statement statement = conn.createStatement()) {

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                result= resultSet.getString("resultado");
            }
            if(result == null){
                result= "{\"header\":" + obtenerCabecera(CODE_NOT_RECORDS, MSG_NOT_RECORDS) + ",\"data\":"+result+"}";
            }
            else{
                result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":"+result+"}";
            }
            resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
        
     
/* Query para obtener todas las colas en base al arreglo de usuarios JSON que espera como parametro 
 * 
 * Ejemplo de JsonRequest
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idcola": 10000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}, {"idcola": 20000, "descripcion":"DescripcionCola", "idestablecimiento":2, "cupos":3,"ultimoatendido":null}]
 * 
 * */   
     
     public String obtenerTipoEmpresaByID(String json) {
        String result="";
        String sql = "select\n" +
            "	json_agg(\n" +
            "    json_build_object(\n" +
            "        'idtipo', te.id_tipo_empresa ,\n" +
            "        'tipo', te.tipo_empresa, \n" +
            "        'descripcion', te.descripcion, \n" +
            "        'imagen', te.imagen \n" +
            "    )\n" +
            "    ) as resultado\n" +
            "FROM tipo_empresa te\n" +
            "where te.id_tipo_empresa  in (select  (json_array_elements_text('"+json+"')::jsonb->'idtipo')::int4 as id)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                result= resultSet.getString("resultado");
            }
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":"+result+"}";
            resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
     
/* Query para insertar los tipo de empresa enviados como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idtipo": 10000, "tipo":"Banco"}, {"idtipo": 20000, "tipo":"Farmacia"}]
 * 
 * */
     
     public String insertarTipoEmpresa(String json) {
        String result="";
        String sql = "insert into tipo_empresa (tipo_empresa, descripcion, imagen)\n" +
            "select trim(tipo) as tipo, trim(descripcion) as descripcion, trim(imagen) as imagen from   json_to_recordset('"+json+"')\n" +
            "		as x(\"idtipo\" text, \"tipo\" text, \"descripcion\" text, \"imagen\" text)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
     
     
/* Query para modificar los tipos de empresa enviados como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idtipo": 10000, "tipo":"Banco"}, {"idtipo": 20000, "tipo":"Farmacia"}]
 * 
 * */
     
     
     public String modificarTipoEmpresa(String json) {
        String result="";
        String sql = "UPDATE tipo_empresa \n" +
            "SET tipo_empresa = coalesce (usRecord.tipo, tipo_empresa.tipo_empresa , usRecord.tipo),\n" +
            "	descripcion = coalesce (usRecord.descripcion, tipo_empresa.descripcion , usRecord.descripcion),\n" +
            "	imagen = coalesce (usRecord.imagen, tipo_empresa.imagen , usRecord.imagen)\n" +
            "FROM (\n" +
            "select * from   json_to_recordset('"+json+"')\n" +
            "		as x(\"idtipo\" text, \"tipo\" text, \"descripcion\" text, \"imagen\" text)\n" +
            ") AS usRecord\n" +
            "WHERE \n" +
            "    usRecord.idtipo::int4 = tipo_empresa.id_tipo_empresa ";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }
  
 /* Query para eliminar los tipos de empresas enviados como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idtipo": 10000, "tipo":"Banco"}, {"idtipo": 20000, "tipo":"Farmacia"}]
 * 
 * */  
     
     
     public String eliminarTipoEmpresa(String json) {
        String result="";
        String sql = "   delete from tipo_empresa te \n" +
            "   where te.id_tipo_empresa in (\n" +
            "	select idtipo::int4 from   json_to_recordset('"+json+"')\n" +
            "			as x(\"idtipo\" text, \"tipo\" text)\n" +
            "	)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.err.println("El sql generado es : "+ sql);

            ps.executeUpdate();
            
            result= "{\"header\":" + obtenerCabecera(CODE_SUCCESS, MSG_SUCESS) + ",\"data\":["+result+"]}";
            //resultSet.close();
            conn.close();

        } catch (SQLException e) {
            System.err.format("Ups, ocurrio un error SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        } catch (Exception e) {
            result= "{\"header\":" + obtenerCabecera(CODE_ERROR, MSG_ERROR + ": "+e.getMessage()) + ",\"data\":["+result+"]}";
        }
        return result;
    }     
     
     
}
