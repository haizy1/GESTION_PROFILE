package hazy.gestionProfile.UsersProfiles;

import hazy.gestionProfile.openfeign.AuthServiceClient;
import hazy.gestionProfile.openfeign.ReqRes;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EtudiantService {


    private final EtudiantRepository etudiantRepository;
    private final EtudiantMapper etudiantMapper;
    //saving part 1
    //todo save is working in postman
//    public Integer save(EtudiantRequest request) {
//        var etudiant = etudiantMapper.toEtudiant(request);
//        return etudiantRepository.save(etudiant).getId();}

    @Autowired
    private  AuthServiceClient authServiceClient;



    // Save profile with user data from AuthService
    public EtudiantResponse saveProfile(EtudiantRequest etudiantRequest, String token) {
        // Fetch user details from auth-service using Feign client
        ReqRes userDetails = authServiceClient.getUserDetails(token);

        if (userDetails.getStatusCode() != 200) {
            throw new RuntimeException("Unable to fetch user details: " + userDetails.getMessage());
        }

        // Map the fetched user details to EtudiantRequest using the mapper
        etudiantRequest = etudiantMapper.fromExternalDataToRequest(userDetails, etudiantRequest);

        // Map EtudiantRequest to Etudiant entity
        Etudiant entity = etudiantMapper.toEtudiant(etudiantRequest);

        // Save the entity in the repository
        Etudiant savedEntity = etudiantRepository.save(entity);

        // Map saved entity to response
        return etudiantMapper.toProductResponse(savedEntity);
    }


    // get user profile by id
    public EtudiantResponse findById(Integer id) {
        return etudiantRepository.findById(id)
                .map(etudiantMapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Etudiant not found with ID:: " + id));
    }

    public List<EtudiantResponse> findAll(){
        return etudiantRepository.findAll()
                .stream().map(etudiantMapper::toProductResponse)//thrown if the entity coudl not  found in the data base
                .collect(Collectors.toList());
    }



    //todo communication entre les micorservices
    public boolean existeEtudiant(int id) {
        return etudiantRepository.existsById(id);  // Utilisation de la méthode JPA pour vérifier l'existence
}


}
