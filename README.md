# 🎯 HackerTracker

A comprehensive LeetCode progress tracking and intelligent scheduling platform designed to help you master the **LeetCode Top 150 Questions**. HackerTracker combines progress tracking, smart scheduling, and a rules based recommendation system to optimize your coding interview preparation.

## Features

### 📊 Progress Tracking
- **Problem Completion Logging**: Track which LeetCode Top 150 questions you've completed with detailed notes
- **Performance Metrics**: Monitor your progress across difficulty levels (Easy, Medium, Hard) and topics (Arrays, Strings, Trees, etc.)
- **Completion Rate Analytics**: Visualize your journey with an eye-opening progress dashboard.

### ⏱️ Time Management
- **Session Timing**: Time your problem-solving sessions to understand your pace and efficiency
- **Historical Tracking**: Review your progress history to identify improvement areas

### 📅 Smart Scheduling
- **Personalized Schedules**: Create custom study schedules based on your target timeline and availability
- **Rules-based Recommender**: The scheduler analyzes:
  - How you rank questions (difficulty perception vs. actual difficulty)
  - Your completion history by topic and difficulty
  - Your learning pace and patterns
  - Available time slots
- **Dynamic Recommendations**: Receive personalized next-question suggestions to optimize your preparation

### 👥 User Management
- **Secure Registration**: Create accounts with password authentication
- **User Profiles**: Customize your learning preferences and goals
- **Progress Persistence**: All your data is saved and synchronized across sessions

### 🔍 Advanced Search
- **Full-Text Search**: Search problems by title, description, and tags using Hibernate Search
- **Topic Filtering**: Browse and filter questions by topic and difficulty
- **Smart Tagging**: Organize problems with custom tags for better organization

## Screenshots

### Main Dashboard
<div style="display: flex; gap: 20px;">
  <div style="flex: 1;">
    <h4>Light Mode</h4>
    
![Main Dashboard - Light Mode](docs/screenshots/main_dashboard.png)
  </div>
  <div style="flex: 1;">
    <h4>Dark Mode</h4>
    
![Main Dashboard - Dark Mode](docs/screenshots/main_dashboard_dark.png)
  </div>
</div>

### Progress Analytics
<div style="display: flex; gap: 20px;">
  <div style="flex: 1;">
    
![Progress Dashboard](docs/screenshots/progress_dashboard.png)
  </div>
  <div style="flex: 1;">
    
![Progress Dashboard Alternative](docs/screenshots/progress_dashboard_bis.png)
  </div>
</div>

## Tech Stack

| Component | Technology |
|-----------|-----------|
| **Backend Framework** | Spring Boot 3.4.4 |
| **Java Version** | Java 21 |
| **Security** | Spring Security with JWT |
| **Database** | MySQL |
| **ORM** | Hibernate 6.6.13 |
| **Search Engine** | Hibernate Search (Apache Lucene) |
| **Caching** | EHCache with JCache |
| **Frontend** | JSP with HTML/CSS/JavaScript |
| **Styling** | SCSS |
| **Build Tool** | Maven |

## Prerequisites

- **Docker** ([Download](https://www.docker.com/products/docker-desktop))
- **Docker Compose** (included with Docker Desktop)
- **Git** ([Download](https://git-scm.com/))

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/hackertracker.git
cd Hackertracker/
```

### 2. Start the Application

The app automatically loads 150 LeetCode Top problems into the database on startup via SeedDataLoader.

### 3. Configure Docker Compose

Copy and customize the Docker Compose configuration:

```bash
cp docker-compose.example.yml docker-compose.yml
```

Edit `docker-compose.yml` to set your preferred database credentials. Example configuration:

```yaml
services:
  db:
    image: mysql:9.2
    environment:
      MYSQL_DATABASE: hackertracker_db
      MYSQL_USER: hack_user
      MYSQL_PASSWORD: hack_pass123
      MYSQL_ROOT_PASSWORD: root_pass123
      TZ: UTC
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql/data
  
  app:
    build: .
    depends_on:
      - db
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/hackertracker_db?createDatabaseIfNotExist=true
      SPRING_DATASOURCE_USERNAME: hack_user
      SPRING_DATASOURCE_PASSWORD: hack_pass123
      JWT_SECRET: your-secret-key-here
      APP_SEED_DATA_ENABLED: "true"
      APP_SEED_DATA_FILE: classpath:data/leetcode_problems.json

volumes:
  mysql_data:
```

### 4. Build and Start Services

```bash
# Build and start all services
docker-compose up --build

# Run in background
docker-compose up -d --build

# View logs
docker-compose logs -f app
```

The application will be available at `http://localhost:8080`

On first run:
1. Docker builds the application image
2. MySQL container starts
3. Spring Boot app starts and:
   - Creates all database tables via Flyway migrations
   - Runs SeedDataLoader to populate 150 LeetCode problems from JSON
   - Initializes the search index
4. App is ready with seed data pre-loaded

**Updating seed data:**
```bash
# Edit src/main/resources/data/leetcode_problems.json as needed

# Restart with fresh database
docker-compose down -v  # Clear database
docker-compose up       # Restart - SeedDataLoader will reload from JSON
```

### 5. Stop the Application

```bash
# Stop services
docker-compose down

# Stop and remove database volume (fresh start)
docker-compose down -v
```

## Validation Rules

### User Registration
- **Username**: 3-20 characters, alphanumeric with underscores
- **Email**: Valid email format
- **Password**: Minimum 8 characters, must include uppercase, lowercase, number, and special character
- **Note**: All usernames and emails must be unique

### User Update
- **Email**: Optional, must be valid email format if provided
- **Name Fields**: Optional, 1-50 characters if provided

### Problem Completion
- **Notes**: Optional, maximum 5000 characters
- **Time Taken**: Positive integer representing minutes
- **Problem ID**: Must refer to valid problem in database

## Testing

Tests run during the Docker build process. To run tests explicitly:

```bash
# Run tests in isolated Docker container
docker-compose exec app mvn test

# Run tests for specific module
docker-compose exec app mvn test -Dtest=UserRepositoryTest

# Run tests with coverage report
docker-compose exec app mvn test jacoco:report

# View coverage report (generates in target/site/jacoco/)
docker-compose exec app cat target/site/jacoco/index.html
```

To skip tests during build (faster):

```bash
docker-compose build --no-cache -- --build-arg MAVEN_SKIP_TESTS=true
```

Edit `Dockerfile` to control test behavior:

```dockerfile
# Skip tests during build
RUN ./mvnw clean package -DskipTests

# Or run tests
RUN ./mvnw clean package
```

## Database Management

### Access Database

```bash
# Connect to MySQL in Docker
docker exec -it hackertracker_db mysql -u hack_user -p

# Or from your host machine
mysql -h 127.0.0.1 -P 3306 -u hack_user -p
```

### View Data

```sql
USE hackertracker_db;
SHOW TABLES;

-- View users
SELECT id, username, email, created_at FROM user;

-- View completion statistics
SELECT u.username, COUNT(c.id) as problems_completed
FROM user u
LEFT JOIN user_problem_completion c ON u.id = c.user_id
GROUP BY u.id;
```

### Reset Database (Development Only)

```bash
# Remove everything and start fresh
docker-compose down -v && docker-compose up --build
```

## Maintenance Commands

### View Logs & Status

```bash
# Application logs in real-time
docker-compose logs -f app

# Database logs
docker-compose logs -f db

# Last 50 lines of app logs
docker-compose logs --tail 50 app

# Search for errors
docker-compose logs app | grep ERROR

# List running containers
docker-compose ps

# Resource usage
docker stats
```

### Control Services

```bash
# Restart containers
docker-compose restart

# Rebuild and restart
docker-compose up --build

# Stop all containers
docker-compose stop

# Stop and remove containers
docker-compose down

# Remove containers and volumes (fresh start)
docker-compose down -v
```

### Clean Up

```bash
# Remove stopped containers
docker container prune

# Remove unused images and volumes
docker image prune && docker volume prune

# Remove everything unused
docker system prune -a
```

## Architecture

### Technology Choices

| Component | Choice | Rationale |
|-----------|--------|-----------|
| **Spring Boot** | Latest stable (3.4.4) | Modern features, excellent ecosystem, easy deployment |
| **JWT Security** | Spring Security + JJWT | Stateless authentication, ideal for REST APIs |
| **Hibernate Search** | Apache Lucene backend | Fast full-text search on LeetCode problems |
| **Caching** | EHCache | Reduces database load, improves performance |
| **JSP Templates** | Server-side rendering | Quick development, suitable for this project scope |
| **Database Migrations** | Flyway | Version control for database schema |
| **Seed Data** | Spring ApplicationRunner | Flexible, idempotent data loading from JSON |
### Security

**JWT Token Management:**
- Configure token lifespan in `application.properties`
- Use strong, randomly-generated secret keys in production
- Store tokens in secure httpOnly cookies (not localStorage)
- Implement refresh token endpoints for long-term sessions
- Always enforce HTTPS in production
### Database Schema Changes

To modify the schema (add tables, columns, indexes), create new Flyway migrations:

**Quick start:**
1. Create `src/main/resources/db/migration/V2__description.sql`
2. Write your ALTER/CREATE statements
3. Restart the app - Flyway applies automatically

**Full guide:** See [SEED_DATA_ARCHITECTURE.md - Database Schema Migrations](SEED_DATA_ARCHITECTURE.md#database-schema-migrations) for naming conventions, testing, best practices, and troubleshooting.

### Database Schema Highlights

**Key Entities:**
- **User**: Stores user accounts and authentication data
- **Problem**: LeetCode Top 150 problems with metadata
- **UserProblemCompletion**: Tracks user's problem-solving history
- **UserSchedule**: Personalized study schedules
- **Topic**: Problem categories (Arrays, Strings, Trees, etc.)
- **Tag**: Custom tags for better organization

**Relationships:**
- User → One-to-Many → UserProblemCompletion (user solves many problems)
- User → One-to-One → UserSchedule (each user has one active schedule)
- Problem → Many-to-Many → Topic (problems can belong to multiple topics)
- UserProblemCompletion → Many-to-Many → Tag (completions can have multiple tags)

## Project Structure

```
src/
├── main/
│   ├── java/com/hackertracker/
│   │   ├── auth/              # Authentication & authorization logic
│   │   ├── config/            # Spring configurations (security, cache, etc.)
│   │   ├── controllers/       # REST API endpoints & request handlers
│   │   ├── dao/               # Data Access Objects (repository layer)
│   │   ├── dto/               # Data Transfer Objects (API models)
│   │   ├── indexer/           # Hibernate Search indexing logic
│   │   ├── problem/           # Problem entity & business logic
│   │   ├── schedule/          # Scheduling & recommendation engine
│   │   ├── security/          # Security configurations & filters
│   │   ├── tag/               # Tag management
│   │   ├── topic/             # Topic management
│   │   ├── user/              # User entity & services
│   │   └── validator/         # Input validation logic
│   └── resources/
│       ├── application.properties
│       ├── static/            # CSS, JavaScript, images
│       └── templates/         # JSP views
└── test/                       # Unit and integration tests

hibernate-search-indexes/      # Full-text search index (auto-generated)
target/                        # Build artifacts
```

## Troubleshooting

### Container won't start

**Problem**: `docker-compose up` fails or container exits

**Solutions:**
```bash
# Check logs for errors
docker-compose logs app

# Rebuild without cache
docker-compose build --no-cache

# Verify docker-compose.yml syntax
docker-compose config

# Make sure Docker daemon is running
docker ps
```

### Port already in use

**Problem**: Error `Bind for 0.0.0.0:3306 or 8080 failed`

**Solutions:**
```bash
# Change ports in docker-compose.yml
ports:
  - "8081:8080"  # Use 8081 instead of 8080
  - "3307:3306"  # Use 3307 instead of 3306

# Or find and kill the process
lsof -i :8080
kill -9 <PID>
```

### Seed data or database issues

**Problem**: Database is empty or SeedDataLoader not loading problems

**Solutions:**
```bash
# Verify JSON file exists in correct location
ls -la src/main/resources/data/leetcode_problems.json

# Check SeedDataLoader logs
docker-compose logs app | grep -i \"seed\\|loading\\|problems\"

# Verify services are running
docker-compose ps

# Fresh start with clean database
docker-compose down -v && docker-compose up --build

# Confirm seed data loaded
docker-compose exec app mysql -h db -u hack_user -phack_pass123 hack_db -e \"SELECT COUNT(*) FROM problem;\"
```

### Permission denied errors

**Problem**: `docker: permission denied while trying to connect to Docker daemon`

**Solutions:**
```bash
# Add user to docker group (Linux)
sudo usermod -aG docker $USER
newgrp docker

# Or use sudo
sudo docker-compose up

# Restart Docker daemon (macOS/Windows)
# Use Docker Desktop GUI
```

### Build fails with Maven errors

**Problem**: Docker build fails with Maven compilation errors

**Solutions:**
```bash
# Check Dockerfile for syntax errors
cat Dockerfile

# Clean build without cache
docker-compose build --no-cache --progress=plain

# Check logs for specific error
docker-compose logs app | tail -50

# Verify all source files exist
git status  # Ensure no files are deleted
```

### Health check fails

**Problem**: Container exits with health check failure

**Solutions:**
```bash
# Wait longer for app startup (increase timeout)
# Edit docker-compose.yml healthcheck section

# Check if MySQL is ready before app starts
docker-compose logs db

# Reduce memory/CPU limits if app is slow to start
docker-compose.yml: resources limit
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Check existing issues for similar problems
- Provide detailed error messages and logs when reporting bugs

---

**Happy coding! Good luck with your LeetCode preparation! 🚀**