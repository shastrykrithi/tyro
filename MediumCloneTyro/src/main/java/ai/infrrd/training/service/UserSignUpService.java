package ai.infrrd.training.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ai.infrrd.training.dto.UserDto;
import ai.infrrd.training.exception.BusinessException;
import ai.infrrd.training.model.Users;
import ai.infrrd.training.repository.UserRepository;

@Service
public class UserSignUpService implements UserService {

	@Autowired
	UserRepository userRepo;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private MongoUserDetailsService userDetailsService;
	
	@Override
	public boolean addUser(UserDto userData) throws BusinessException {
		Users user=new Users();
		user.setUsername(userData.getUsername());
		user.setEmail(userData.getEmail());
		user.setPassword(passwordEncoder.encode(userData.getPassword()));
		userRepo.save(user);
		return true;
	}

	public Optional<UserDto> getByEmailAndPassword(String email, String password) throws BusinessException {
		UserDetails user=userDetailsService.loadUserByUsername(email);
		if(passwordEncoder.matches(password, user.getPassword())) {
			return Optional.ofNullable(userRepo.findByEmail(email));
		}
		else {
			throw new BusinessException("Password not match");
		}
	
			
			

}
}
