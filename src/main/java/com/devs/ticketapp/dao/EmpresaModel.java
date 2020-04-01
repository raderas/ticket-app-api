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
public class EmpresaModel extends CommonsDAO{
    
    
/* Query para obtener todos las empresas de la tabla 
 * 
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idempresa": 10000, "nombre":"Davivienda", "nombrecorto":"Davi", "tipoempresa":3,"nit":null}, {"idempresa": 10000, "nombre":"Banco Agricola", "nombrecorto":"BA", "tipoempresa":3,"nit":null}]
 * 
 * */ 
    
     public String obtenerTotalEmpresas() {
        String result="";
        String sql = "select\n" +
                "	json_agg(\n" +
                "    json_build_object(\n" +
                "        'idempresa', e.id_empresa ,\n" +
                "        'nombre', e.nombre ,\n" +
                "        'nombrecorto', e.nombre_corto ,\n" +
                "        'tipoempresa', e.id_tipo_empresa ,\n" +
                "        'nit', e.nit \n" +
                "    )\n" +
                "    ) as resultado\n" +
                "FROM empresa e";

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
        
     
/* Metodo para obtener todas las empresas en base al arreglo de usuarios JSON que espera como parametro 
 * 
 * Ejemplo de JsonRequest
 * [{"idempresa": 10000, "nombre":null, "nombrecorto":null, "tipoempresa":null,"nit":null},{"idempresa": 10000, "nombre":null, "nombrecorto":null, "tipoempresa":null,"nit":null}]
 * 
 * Ejemplo de JsonResponse
 * 
 * [{"idempresa": 10000, "nombre":"Davivienda", "nombrecorto":"Davi", "tipoempresa":3,"nit":null}, {"idempresa": 10000, "nombre":"Banco Agricola", "nombrecorto":"BA", "tipoempresa":3,"nit":null}]
 * 
 * */   
     
     public String obtenerEmpresaByID(String json) {
        String result="";
        String sql = "select\n" +
            "	json_agg(\n" +
            "    json_build_object(\n" +
            "        'idempresa', e.id_empresa ,\n" +
            "        'nombre', e.nombre ,\n" +
            "        'nombrecorto', e.nombre_corto ,\n" +
            "        'tipoempresa', e.id_tipo_empresa ,\n" +
            "        'nit', e.nit \n" +
            "    )\n" +
            "    ) as resultado\n" +
            "FROM empresa e\n" +
            "where e.id_empresa  in (select  (json_array_elements_text('"+json+"')::jsonb->'idempresa')::int4 as id)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

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
     
/* Query para insertar la empresa enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idempresa": 10000, "nombre":"Davivienda", "nombrecorto":"Davi", "tipoempresa":3,"nit":null}, {"idempresa": 10000, "nombre":"Banco Agricola", "nombrecorto":"BA", "tipoempresa":3,"nit":null}]
 * 
 * */
     
     
     public String insertarEmpresa(String json) {
        String result="";
        String sql = "insert into empresa (nombre , nombrecorto , id_tipo_empresa , nit )\n" +
                    "select trim(nombre) as nombre, trim(nombrecorto) as nombrecorto, id_tipo_empresa::int4, trim(nit) as nit from   json_to_recordset('"+json+"')\n" +
                    "		as x(\"idempresa\" text, \"nombre\" text, \"nombrecorto\" text, \"tipoempresa\" text, \"nit\" text)";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

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
     
     
/* Query para modificar la empresa enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idempresa": 10000, "nombre":"Davivienda", "nombrecorto":"Davi", "tipoempresa":3,"nit":null}, {"idempresa": 10000, "nombre":"Banco Agricola", "nombrecorto":"BA", "tipoempresa":3,"nit":null}]
 * 
 * */	
     
     
     public String modificarEmpresa(String json) {
        String result="";
        String sql = "UPDATE empresa\n" +
                "SET nombre = coalesce (usRecord.nombre, empresa.nombre, usRecord.nombre), \n" +
                "    nombre_corto = coalesce (usRecord.nombrecorto, empresa.nombre_corto , usRecord.nombrecorto),\n" +
                "    id_tipo_empresa =coalesce (usRecord.tipoempresa, empresa.id_tipo_empresa , usRecord.tipoempresa::int4),\n" +
                "    nit =coalesce (usRecord.nit, empresa.nit , usRecord.nit)\n" +
                "FROM (\n" +
                "select * from   json_to_recordset('"+json+"')\n" +
                "		as x(\"idempresa\" text, \"nombre\" text, \"nombrecorto\" text, \"tipoempresa\" text, \"nit\" text)\n" +
                ") AS usRecord\n" +
                "WHERE \n" +
                "    usRecord.idempresa::int4 = empresa.id_empresa";

        try (Connection conn = DriverManager.getConnection(
                getUrl(), getUser(), getPassword());
                PreparedStatement ps = conn.prepareStatement(sql)) {
            
            //System.err.println("El sql generado es : "+ sql);

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
  
 /* Query para eliminar la empresa enviada como parametro en un arreglo Json 
 * 
 * Ejemplo de JsonRequest
 * [{"idempresa": 10000, "nombre":"Davivienda", "nombrecorto":"Davi", "tipoempresa":3,"nit":null}, {"idempresa": 10000, "nombre":"Banco Agricola", "nombrecorto":"BA", "tipoempresa":3,"nit":null}]
 * 
 * */  
     
     
     public String eliminarEmpresa(String json) {
        String result="";
        String sql = "   delete from empresa e \n" +
                "   where e.id_empresa in (\n" +
                "	select idempresa::int4 from   json_to_recordset('"+json+"')\n" +
                "			as x(\"idempresa\" text, \"nombre\" text, \"nombrecorto\" text, \"tipoempresa\" text, \"nit\" text)\n" +
                "	) AS usRecord";

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
