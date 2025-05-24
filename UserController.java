@RestController
public class UserController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private EmailService emailService;

    @PostMapping("/users")
    public void createUser(@RequestBody UserDto dto) {
        User user = new User(dto.getName(), dto.getEmail());
        repo.save(user);
        emailService.sendWelcomeEmail(user.getEmail());
    }
}