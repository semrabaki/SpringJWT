package com.springJWT.reqres;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter //SAdece username ve passwordu okuyup yazicaz bisey oluisturmucaz o sebeple getter ve setter yeterli
public class LoginRequest {
    private String username;
    private String password;

}
