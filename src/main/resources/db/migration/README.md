# Database Migrations

This directory contains Flyway SQL migrations that run automatically on application startup.

## Files

- **V1__create_schema.sql** - Creates database schema (tables, relationships, constraints)

Flyway executes all versioned migrations in order and tracks them in the `flyway_schema_history` table. Each migration runs only once.

## See Also

- **Seed Data Setup**: [src/main/resources/data/README.md](../data/README.md)
- **Architecture Details**: [SEED_DATA_ARCHITECTURE.md](../../../SEED_DATA_ARCHITECTURE.md)




