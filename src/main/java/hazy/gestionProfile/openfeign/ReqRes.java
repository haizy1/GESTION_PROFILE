package hazy.gestionProfile.openfeign;

import hazy.gestionProfile.enumm.Genre;
import hazy.gestionProfile.enumm.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ReqRes {

    private int id; // Add this field for user ID
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String nom;
    private String prenom;
    private String motdepasse;
    private String adresse;
    private String numtele;
    private Role role;
    private Genre genre;
    private String email;
}