package domains.user;

import enums.Role;
import exceptions.UnauthorizedRequestException;
import utils.APIUtils;
import utils.Constants;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException(Constants.USER_NOT_FOUND));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User entity) {
        if (existsByUsername(entity.getUsername())) {
            throw new IllegalArgumentException(Constants.USERNAME_ALREADY_EXISTS);
        }

        return userRepository.save(entity);
    }

    public void update(User entity) {
        userRepository.update(entity);
    }

    public void delete(User entity) {
        userRepository.delete(entity);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException(Constants.USER_NOT_FOUND));
    }

    public User findByToken(String token) {
        return userRepository.findByToken(token).orElseThrow(() -> new NoSuchElementException(Constants.TOKEN_NOT_FOUND));
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public String generateToken() {
        Random random = new Random();
        StringBuilder token = new StringBuilder();

        random.ints(10, 0, 10).forEach(token::append);

        return token.toString();
    }

    public String login(String username, String password) {
        User user = findByUsername(username);

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException(Constants.INVALID_PASSWORD);
        }

        var token = generateToken();
        user.setToken(token);
        userRepository.update(user);

        return token;
    }

    public void logout(String token) {
        User user = findByToken(token);
        user.setToken(null);
        userRepository.update(user);
    }

    public User authorizeUserByRole(String authorizationHeader) throws UnauthorizedRequestException {
        String headerToken = APIUtils.extractTokenFromAuthorizationHeader(authorizationHeader);

        User user = findByToken(headerToken);

        Set<Role> acceptedRoles = Set.of(Role.ADMIN, Role.MANAGER);

        if (!acceptedRoles.contains(user.getRole())) {
            throw new UnauthorizedRequestException();
        }

        return user;
    }

    public User authorizeUserByRole(String authorizationHeader, Set<Role> acceptedRoles) throws UnauthorizedRequestException {
        String headerToken = APIUtils.extractTokenFromAuthorizationHeader(authorizationHeader);

        User user = findByToken(headerToken);

        if (!acceptedRoles.contains(user.getRole())) {
            throw new UnauthorizedRequestException();
        }

        return user;
    }
}
