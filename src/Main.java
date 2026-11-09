import domain.Friendship;
import domain.Tuple;
import domain.User;
import repository.InMemoryRepository;
import repository.Repository;
import repository.RepositoryFactory;
import service.CommunityService;
import service.FriendshipService;
import service.UserService;
import ui.Console;
import validator.FriendshipValidator;
import validator.UserValidator;
import validator.Validator;
import validator.ValidatorException;

public class Main{
    public static void main(String[] args)
    {
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        Validator<User> baseValidator = new UserValidator();
        Validator<User> ageValidator = user ->{
            if(user.getVarsta() < 14)
                throw new ValidatorException("Utilizatorul trebuie sa aiba cel putin 14 ani");
        };
        Validator<User> userValidator = baseValidator.andThen(ageValidator);

        RepositoryFactory factory = RepositoryFactory.getInstance();
        Repository<Integer, User> userInMemoryRepository = factory.createUserRepository(userValidator);
        Repository<Tuple<Integer, Integer>, Friendship> friendshipInMemoryRepository = factory.createFriendshipRepository(friendshipValidator);

        UserService userService = new UserService(userInMemoryRepository);
        FriendshipService friendshipService = new FriendshipService(userInMemoryRepository, friendshipInMemoryRepository);
        CommunityService communityService = new CommunityService(userInMemoryRepository, friendshipInMemoryRepository);

        Console console = new Console(userService, friendshipService, communityService);
        console.run();

    }
}