package com.pjh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yueyinghaibao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LecUser {
    private Integer id;
    private String password;
    private String name;
    private String address;
    private Integer port;
    private Byte status;
}
