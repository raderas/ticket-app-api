/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.devs.ticketapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author azus
 */
public class User {
    
    @Getter @Setter
    private String user;
    private String token;
        
}
