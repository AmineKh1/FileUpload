package ma.ynmo.cdn.services.Impl;

import lombok.extern.slf4j.Slf4j;
import ma.ynmo.cdn.exception.EntityNotFoundException;
import ma.ynmo.cdn.exception.ErrorCodes;
import ma.ynmo.cdn.exception.InvalidEntityException;
import ma.ynmo.cdn.model.User;
import ma.ynmo.cdn.repository.UserRepository;
import ma.ynmo.cdn.services.UserServices;
import ma.ynmo.cdn.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServiceImpl implements UserServices {
    private UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }
    @Override
    public Mono<User> save(User obj) {


        return UserServiceImpl.validateUser(obj)
                    .flatMap(userRepository::save)
                .doOnSuccess(System.out::println);
    }

        public static  Mono<User> validateUser(User user) {
                return Mono.just(UserValidator.validate(user))
                        .flatMap(errrs-> {
                            if (!errrs.isEmpty())
                                    return Mono.error(new InvalidEntityException("User is not valid", ErrorCodes.USER_NOT_VALID, errrs));
                        return Mono.just(user);
                        });
        }
    @Override
    public Mono<User> findById(Long id) {
        if(id==null) {
            log.error("USER ID IS NULL");
            return null;
        }
        Mono<User> user= userRepository.findById(id);
        return Optional.of(user).orElseThrow(() -> new EntityNotFoundException("No user id ="+id+"exist in DB",ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public Mono<User> findByEmail(String email) {
        if(email==null) {
            log.error("USER ID IS NULL");
            return null;
        }
        Mono<User> user= userRepository.findByEmail(email);
        return Optional.of(user).orElseThrow(() -> new EntityNotFoundException("No user with email ="+email+"exist in DB",ErrorCodes.USER_NOT_FOUND));
    }

    @Override
    public Flux<User> findAll() {
        return userRepository.findAll();

    }

    @Override
    public void delete(Long id) {
        if(id==null) {
            log.error("USER ID IS NULL");
            return;
        }
        userRepository.deleteById(id);

    }
}
