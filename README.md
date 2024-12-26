Setup Instructions and Code Architecture Details:
Below are the steps and details related to the setup and architecture of the application.

1. Setup Instructions
To get this project up and running, follow these steps:

a)Prerequisites
Java 8+ installed
Maven or Gradle (build tool)
Spring Boot (preferably the latest stable version)
Redis running locally or remotely (for caching)

b)Steps for Setup:
Clone the Repository: If this code is stored in a Git repository, you can clone it using:
	git clone https://github.com/mayankchandrol201997/Assignment
	cd Assignment

c)Configure Application Properties: In the application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=validate

spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=

server.port=8080

d)Install Dependencies: If you're using Maven, run the following command to install dependencies:
	mvn clean install

e)Run the Application: To run the application using Spring Boot’s embedded server:
mvn spring-boot:run
Or if you are using Gradle:


f)Access the Application: Once the application is running, you can access the API endpoints via:

POST /user - To create a user
GET /user/{id} - To retrieve a user by ID
GET /user/fetch - To fetch users concurrently
GET /user - To retrieve all users
DELETE /user/{id} - To delete a user by ID
PUT /user/{id} - To update a user by ID

2. Code Architecture Details
The application follows a layered architecture, with separation of concerns between the web layer (controller), service layer (business logic), and data access layer (repository). Here's a breakdown:

a)Controller Layer (UserController)
The UserController class is responsible for handling HTTP requests and mapping them to service methods. It uses the @RestController and @RequestMapping annotations to expose RESTful API endpoints.

@PostMapping – Creates a new user.
@GetMapping("/{id}") – Retrieves a user by ID.
@GetMapping("/fetch") – Fetches multiple users concurrently by their IDs.
@GetMapping – Fetches all users.
@DeleteMapping("/{id}") – Deletes a user by ID.
@PutMapping("/{id}") – Updates an existing user by ID.
The controller makes use of the UserServiceImpl for business logic and response transformation.

b)Service Layer (UserServiceImpl)
The UserServiceImpl class contains the core business logic for managing users. It uses the UserRepository for database operations and interacts with the Redis cache for user data retrieval.

createUser – Saves a new user to the database and returns the response DTO.
getUserById – Retrieves a user either from the Redis cache or database, with cache updates and fallbacks.
getAllUser – Fetches all users from the database.
updateUser – Updates an existing user's details and invalidates the Redis cache.
deleteUser – Deletes a user and invalidates the cache for that user.
fetchUsersConcurrently – Retrieves multiple users concurrently using ExecutorService and Java's Callable and Future interfaces to handle parallelism.

c)Repository Layer (UserRepository)
UserRepository would typically extend JpaRepository<User, Long> and handle CRUD operations for the User entity.

d)Cache Layer (RedisCacheService)
RedisCacheService is responsible for interacting with Redis for caching user data. This service can cache results of user retrievals to speed up subsequent queries. Methods likely include:

getFromCache(String cacheName, String key)
updateToCache(String cacheName, String key, String value)
deleteFromCache(String cacheName, String key)

e)DTO and Mapping
DTOs:
UserRequestDto is used for the input data when creating or updating a user.
UserResponseDto is used for the data returned from the API.

f)Mapping:
The UserMapper is used to convert between User entities and their corresponding DTOs (UserRequestDto and UserResponseDto).

g)Exception Handling
Custom Exceptions:
InvalidRequestException, IdNotFoundException, and UserNotFoundException are used to handle business-specific exceptions.
These exceptions can be mapped to specific HTTP status codes, using @ControllerAdvice to globally handle them.

h)Concurrency
Concurrency Handling:
The fetchUsersConcurrently method is designed to fetch users concurrently using ExecutorService, Callable, and Future. This allows for parallel execution of tasks and is particularly useful when fetching a list of users.

i)Retry Logic
Retryable:
The @Retryable annotation is used on the createUser method to retry failed operations. If the operation fails, it will retry up to 2 times with a delay between retries. This can be useful for handling transient failures (e.g., database connection issues).

3. Libraries & Dependencies
Here are the major dependencies likely used in the project:

Spring Boot - Framework for building RESTful applications.
Spring Data JPA - ORM for interacting with the database.
Spring Redis - For caching data in Redis.
Jackson - For JSON parsing and serialization (ObjectMapper).
Lombok - For reducing boilerplate code (e.g., getters, setters, constructors).
Spring AOP - For handling retry logic (e.g., @Retryable).
ExecutorService - For managing concurrency.
