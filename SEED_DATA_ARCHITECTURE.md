# Seed Data Loading Architecture

This document explains how seed data is loaded into the database using **SeedDataLoader**, a Spring ApplicationRunner that automatically populates the database with 150 LeetCode problems on application startup.

## Overview

**SeedDataLoader** is the single source of truth for seed data loading:
- Reads from `src/main/resources/data/leetcode_problems.json`
- Loads data on application startup
- Idempotent (only loads if database is empty)
- Works in both Docker and local development environments

| Component | Role | Location |
|-----------|------|----------|
| **SeedDataLoader.java** | Spring ApplicationRunner bean | `src/main/java/com/hackertracker/config/` |
| **SeedDataDto.java** | Jackson POJO for JSON deserialization | `src/main/java/com/hackertracker/config/` |
| **leetcode_problems.json** | Source data file with 150 problems | `src/main/resources/data/` |
| **application.properties** | Configuration flags | `src/main/resources/` |

## Startup Sequence

### Docker Startup

```
1. Docker Compose starts MySQL container
2. Docker Compose starts Spring Boot app
3. Spring Boot initializes:
   - Flyway runs migrations (creates tables)
   - SeedDataLoader bean starts:
     * Checks if database has problems
     * If empty → Loads from leetcode_problems.json
     * If data exists → Skips (idempotent)
   - Hibernate Search index initializes
4. App ready with seed data pre-loaded
```

### Local Development Startup

```
1. Run: ./mvnw spring-boot:run
2. Spring Boot initializes:
   - Flyway runs migrations (creates tables)
   - SeedDataLoader checks database:
     * If empty → Loads from classpath:data/leetcode_problems.json
     * If data exists → Skips
3. App ready with seed data pre-loaded
```

## Components

### 1. **SeedDataLoader.java**

Spring `@Configuration` class with `ApplicationRunner` bean that loads seed data on startup.

**Features:**
- Configured via `application.properties` and environment variables
- Reads JSON from configurable source (classpath, file path, S3 in future)
- Idempotent (only loads if `problemRepository.count() == 0`)
- Generates UUIDs automatically
- Transactional for consistency

```java
@Bean
public ApplicationRunner seedDatabase(...) {
    return args -> {
        if (problemRepository.count() > 0) {
            log.info("Database already contains problems, skipping seed data load");
            return;
        }
        // Load from JSON
        // Parse problems, topics, tags
        // Save to database
    };
}
```

**When it runs:**
- After Flyway migrations complete
- Before application becomes ready
- Only once (idempotent check)

### 2. **SeedDataDto.java**

Jackson POJO for deserializing `leetcode_problems.json`. Maps the JSON structure:

```
stat_status_pairs[]
  ├─ stat
  │  ├─ question__title
  │  ├─ question__article__slug
  │  ├─ topic { name }
  │  └─ tags[]
  └─ difficulty { level }
```

### 3. **leetcode_problems.json**

Source file with 150 LeetCode problems in JSON format.

Location: `src/main/resources/data/leetcode_problems.json`

Format:
```json
{
  "stat_status_pairs": [
    {
      "stat": {
        "question__title": "Two Sum",
        "question__article__slug": "two-sum",
        "topic": { "name": "Array" },
        "tags": [
          { "name": "Array" },
          { "name": "Hash Table" }
        ]
      },
      "difficulty": { "level": "Easy" }
    }
  ]
}
```

### 4. **application.properties**

Configuration flags to control seed loading:

```properties
# Enable/disable seed data loading
app.seed-data.enabled=true

# Source location
app.seed-data.file=classpath:data/leetcode_problems.json
```

Can be overridden via environment variables:
```bash
APP_SEED_DATA_ENABLED=true
APP_SEED_DATA_FILE=classpath:data/leetcode_problems.json
```

### 5. **V1__create_schema.sql** (Migration)

Flyway migration that creates the database schema (tables only, no INSERT statements).

Schema tables populated by SeedDataLoader:
- `topic`
- `tag`
- `problem`
- `problem_topic` (join table)
- `problem_tag` (join table)

## Setup & Usage

### Docker Deployment

**First time setup:**

```bash
# 1. Copy docker-compose template
cp docker-compose.example.yml docker-compose.yml

# 2. Edit docker-compose.yml with your database credentials
# 3. Build and start services
docker-compose up --build

# What happens:
# - MySQL starts (empty database)
# - Spring Boot starts
# - Flyway creates tables
# - SeedDataLoader loads 150 problems from JSON
# - App ready with data
```

**Updating seed data:**

```bash
# 1. Edit src/main/resources/data/leetcode_problems.json

# 2. Restart with fresh database
docker-compose down -v           # Remove database volume
docker-compose up                # Restart - SeedDataLoader reloads JSON

# Data reload takes ~3-5 seconds depending on JSON size
```

### Local Development

Ensure JSON exists at `src/main/resources/data/leetcode_problems.json`, then:

```bash
# Run Spring Boot directly
./mvnw spring-boot:run

# First run:
# 1. Flyway creates tables from V1__create_schema.sql
# 2. SeedDataLoader reads JSON and loads 150 problems
# 3. App ready
```

Properties used (from `application.properties`):
```properties
app.seed-data.enabled=true
app.seed-data.file=classpath:data/leetcode_problems.json

# Database connection
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/hackertracker_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:root}
```

## Database Schema Migrations

For detailed Flyway documentation, see: [Flyway Getting Started](https://flywaydb.org/documentation/faq)

### Adding new fields to seed data

**Scenario:** You want to add a new field to problems (e.g., `difficulty_score` as a number, `company_tags` as an array).

**Step 1: Update the JSON file**

Add new fields to `src/main/resources/data/leetcode_problems.json`:
```json
{
  "stat_status_pairs": [
    {
      "stat": {
        "question__title": "Two Sum",
        "difficulty_score": 4.8,
        "company_tags": ["Amazon", "Google", "Facebook"]
      },
      "difficulty": { "level": "Easy" }
    }
  ]
}
```

**Step 2: Update SeedDataDto.java**

Add the new fields to the POJO:
```java
public static class ProblemData {
    @JsonProperty("stat")
    public Stat stat;
    
    public static class Stat {
        @JsonProperty("question__title")
        public String title;
        
        @JsonProperty("difficulty_score")  // NEW
        public Double difficultyScore;
        
        @JsonProperty("company_tags")      // NEW
        public List<String> companyTags;
    }
}
```

**Step 3: Create Flyway migration to add columns**

Create `src/main/resources/db/migration/V2__add_difficulty_score_and_company_tags.sql`:
```sql
ALTER TABLE problem 
  ADD COLUMN difficulty_score DECIMAL(3, 1),
  ADD COLUMN company_tags JSON;
```

**Step 4: Update SeedDataLoader to populate new fields**

Modify `SeedDataLoader.java` to map new fields:
```java
problem.setDifficultyScore(problemData.stat.difficultyScore);
problem.setCompanyTags(String.join(",", problemData.stat.companyTags));
problemRepository.save(problem);
```

**Step 5: Test end-to-end**

```bash
# 1. Add new JSON fields ✓
# 2. Update SeedDataDto ✓
# 3. Add migration ✓
# 4. Update SeedDataLoader ✓
# 5. Test
docker-compose down -v
docker-compose up --build

# Verify new data loaded
docker-compose exec db mysql -u hack_user -phack_pass123 hack_db -e \
  "SELECT difficulty_score, company_tags FROM problem LIMIT 1;"
```

### Adding new relationships

**Scenario:** Problems now belong to companies (many-to-many).

**Step 1: Add company data to JSON**

```json
{
  "stat_status_pairs": [
    {
      "stat": {
        "question__title": "Two Sum",
        "companies": [
          { "name": "Amazon", "frequency": "High" },
          { "name": "Google", "frequency": "Medium" }
        ]
      }
    }
  ]
}
```

**Step 2: Update SeedDataDto**

```java
public static class Company {
    @JsonProperty("name")
    public String name;
    
    @JsonProperty("frequency")
    public String frequency;
}

public static class Stat {
    @JsonProperty("companies")
    public List<Company> companies;
}
```

**Step 3: Create migration for new tables**

Create `src/main/resources/db/migration/V2__add_company_tables.sql`:
```sql
CREATE TABLE company (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE problem_company (
    id VARCHAR(36) PRIMARY KEY,
    problem_id VARCHAR(36) NOT NULL,
    company_id VARCHAR(36) NOT NULL,
    frequency VARCHAR(50),
    FOREIGN KEY (problem_id) REFERENCES problem(id),
    FOREIGN KEY (company_id) REFERENCES company(id),
    UNIQUE KEY (problem_id, company_id)
);
```

**Step 4: Update SeedDataLoader**

```java
for (var problemData : seedData.stat_status_pairs) {
    Problem problem = new Problem();
    problemRepository.save(problem);
    
    // Link companies
    for (var companyData : problemData.stat.companies) {
        Company company = companyRepository.findByName(companyData.name)
            .orElseGet(() -> companyRepository.save(
                new Company(UUID.randomUUID().toString(), companyData.name)
            ));
        
        ProblemCompany link = new ProblemCompany(
            UUID.randomUUID().toString(),
            problem,
            company,
            companyData.frequency
        );
        problemCompanyRepository.save(link);
    }
}
```

**Step 5: Test**

```bash
docker-compose down -v && docker-compose up --build

# Query the data
docker-compose exec db mysql -u hack_user -phack_pass123 hack_db -e \
  "SELECT p.question_title, c.name, pc.frequency FROM problem p 
   LEFT JOIN problem_company pc ON p.id = pc.problem_id 
   LEFT JOIN company c ON c.id = pc.company_id LIMIT 5;"
```

### Best practices

When adding new fields to seed data:

1. **Update JSON** → Add new fields with sample data
2. **Update DTO** → Add `@JsonProperty` annotations for each new field
3. **Create migration** → Add table columns/new tables
4. **Update SeedDataLoader** → Map DTO fields to entity setters
5. **Test** → `docker-compose down -v && docker-compose up --build`

**Common pitfalls:**

| Problem | Solution |
|---------|----------|
| JSON has new field, but migration not run | Migration must exist in `src/main/resources/db/migration/` before startup |
| Migration exists but DTO not updated | Add `@JsonProperty` to all new fields in POJO |
| Schema changes without testing | Always test on clean database: `docker-compose down -v && docker-compose up --build` |

## Troubleshooting

### JSON file not found by SeedDataLoader

**Error:**
```
Seed data file not found: classpath:data/leetcode_problems.json
```

**Solutions:**
- Verify file exists: `ls -la src/main/resources/data/leetcode_problems.json`
- Check `application.properties` path: `app.seed-data.file=classpath:data/leetcode_problems.json`
- Rebuild: `./mvnw clean compile`

### Database is empty after startup

**Symptom:** Container starts but no data in database

**Solutions:**
```bash
# Check how many problems are in database
docker-compose exec db mysql -u hack_user -phack_pass123 hack_db -e "SELECT COUNT(*) FROM problem;"

# View SeedDataLoader logs
docker-compose logs app | grep -i "seed\|loading\|problems"

# Verify JSON file has content
cat src/main/resources/data/leetcode_problems.json | head -20

# Fresh start with clean database
docker-compose down -v && docker-compose up --build
```

### Properties not being read

**Symptom:** `app.seed-data.enabled=false` but data still loads

**Solutions:**
```bash
# Restart app (not hot-reload)
docker-compose restart app

# Check for typos in application.properties
cat src/main/resources/application.properties | grep app.seed

# Verify environment variables are set correctly
docker-compose config | grep APP_SEED
```

### Seed data loads every time (duplicates)

**Symptom:** Database grows with duplicate problems on each restart

**Cause:** SeedDataLoader checks `problemRepository.count() > 0` but finds 0 due to:
- Database volume not persisting
- Flyway migration failed
- Connection issues

**Solutions:**
```bash
# Verify database volume exists
docker volume ls | grep mysql

# Check Flyway migration status
docker-compose logs app | grep -i "flyway\|migration"

# Fresh start ensures proper initialization
docker-compose down -v && docker-compose up --build
```

### SeedDataLoader takes too long

**Symptom:** App startup is slow when loading seed data

**Solutions:**
- JSON parsing and database inserts are typically fast (~2-5 seconds for 150 problems)
- If slower, check:
  ```bash
  # Monitor database CPU/I/O during startup
  docker-compose logs db
  
  # Check if database is under heavy load
  docker stats
  ```

## Future Enhancements

- [ ] Support loading from S3 bucket
- [ ] Support loading from HTTP endpoint
- [ ] Batch inserts for performance optimization
- [ ] Data versioning with checksums
- [ ] Admin UI to trigger seed reload without restart
- [ ] Support for multiple seed files with composition
- [ ] Merge strategy instead of replace

## References

- [Spring Boot ApplicationRunner](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/ApplicationRunner.html)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [Jackson JSON Processing](https://github.com/FasterXML/jackson)
