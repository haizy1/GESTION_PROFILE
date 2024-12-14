package hazy.gestionProfile.Loueur;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Getter
@Setter
@Entity
public class Loueur  {

    @Id
    private int id;

    private String nom;
    private String prenom;

    @OneToMany(mappedBy = "loueur", cascade = CascadeType.ALL)
    private List<Annonce> annonces; // Un Loueur peut avoir plusieurs Annonces


}
