# Seed Data Directory

Place the seed data file here:

```
leetcode_problems.json
```

This file will be automatically loaded on application startup via `SeedDataLoader`.

## Configuration

Control loading behavior in `src/main/resources/application.properties`:

```properties
app.seed-data.enabled=true                              # Enable/disable loading
app.seed-data.file=classpath:data/leetcode_problems.json  # File location
```

## See Also

- **Detailed Setup**: [SEED_DATA_ARCHITECTURE.md](../../../SEED_DATA_ARCHITECTURE.md)

