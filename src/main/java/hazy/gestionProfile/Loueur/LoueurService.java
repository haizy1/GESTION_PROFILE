package hazy.gestionProfile.Loueur;


import hazy.gestionProfile.Comments.Comment.Comment;
import hazy.gestionProfile.openfeign.AuthServiceClient;
import hazy.gestionProfile.openfeign.ReqRes;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class LoueurService {

    private final AnnonceRepository annonceRepository;
    private final LoueurMapper loueurMapper;
    private final LoueurRepository loueurRepository;
    @Autowired
    private AuthServiceClient authServiceClient;

    //saving part 1
    //todo save is working in postman
//    public Integer save(AnnonceRequest request) {
//        var annonce = loueurMapper.toLoueur(request);
//        return loueurRepository.save(annonce).getId();}

    // Save Annonce with user data from AuthService
    public Integer save(AnnonceRequest request, String token) {
        // Fetch user details from auth-service using the provided token
        ReqRes userDetails = authServiceClient.getUserDetails(token);

        if (userDetails.getStatusCode() != 200) {
            throw new RuntimeException("Unable to fetch user details: " + userDetails.getMessage());
        }

        // Create a new Loueur using the userDetails fetched from AuthService
        Loueur loueur = new Loueur();
        loueur.setId(userDetails.getId());  // Set the Loueur ID from the AuthService response
        loueur.setNom(userDetails.getNom());  // Set the Loueur 'nom' from the AuthService response
        loueur.setPrenom(userDetails.getPrenom());  // Set the Loueur 'prenom' from the AuthService response

        // Debugging: Log the Loueur details before saving
        System.out.println("Saving Loueur: " + loueur.getNom() + " " + loueur.getPrenom());

        // Save the Loueur to the database
        Loueur savedLoueur = loueurRepository.save(loueur);

        // Map the request and the newly created Loueur object to an Annonce entity
        Annonce annonce = loueurMapper.toLoueur(request, savedLoueur);

        // Save the Annonce entity
        Annonce savedAnnonce = annonceRepository.save(annonce);

        // Return the ID of the saved Annonce
        return savedAnnonce.getId();
    }



    // get user profile by id
    public AnnonceResponse findById(Integer id) {
        return annonceRepository.findById(id)
                .map(loueurMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Etudiant not found with ID:: " + id));
    }

    public List<AnnonceResponse> findAll(){
        return annonceRepository.findAll()
                .stream().map(loueurMapper::toProductResponse)//thrown if the entity coudl not  found in the data base
                .collect(Collectors.toList());
    }


    //todo communication entre microservices
    public boolean existeAnnonce(int id) {
        return annonceRepository.existsById(id);  // Utilisation de la méthode JPA pour vérifier l'existence
    }



    //consuming the comment from kafka
    @Transactional
    @KafkaListener(topics = "comment-topic", groupId = "comment-group")
    public void consommerCommentaire(Comment commentaire) {
        try {
            // Récupérer l'annonce par l'ID de l'annonce
            Annonce annonce = annonceRepository.findById(commentaire.getAnnonceId())
                    .orElseThrow(() -> new RuntimeException("Annonce non trouvée pour l'ID: " + commentaire.getAnnonceId()));

            // Ajouter le contenu du commentaire à la liste des contenus de commentaires
            annonce.addCommentaire(commentaire.getContenu());

            // Sauvegarder l'annonce mise à jour avec le nouveau contenu de commentaire
            annonceRepository.save(annonce);

            System.out.println("Commentaire ajouté avec succès à l'Annonce ID: " + annonce.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la consommation du commentaire: " + e.getMessage());
        }
    }


//todo hadi pour supprimer loueur
public void deleteById(Integer id) {
    if (!loueurRepository.existsById(id)) {
        throw new IllegalArgumentException("Loueur non trouvé avec l'ID : " + id);
    }
    loueurRepository.deleteById(id);
}
    //todo hadi li ajoutit:
    public void save(Loueur loueurRequest) {
        // Conversion de la requête en entité Loueur
        Loueur loueur = Loueur.builder()
                .id(loueurRequest.getId())
                .nom(loueurRequest.getNom())
                .prenom(loueurRequest.getPrenom())
                .build();

        // Sauvegarde dans le repository
        loueurRepository.save(loueur);
    }

}





