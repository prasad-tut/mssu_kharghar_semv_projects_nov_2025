# Contributing to Bug Tracker

Thank you for your interest in contributing to the Bug Tracker project! This document provides guidelines and instructions for contributing.

## Code of Conduct

- Be respectful and constructive
- Avoid personal attacks or harassment
- Respect different opinions
- Help create a welcoming environment

## How to Contribute

### 1. Report Bugs

If you find a bug, please create an issue with:
- **Title**: Clear and descriptive
- **Description**: What happened and what you expected
- **Steps to Reproduce**: How to reproduce the issue
- **Environment**: Java version, OS, etc.
- **Screenshots**: If applicable

### 2. Suggest Features

Share feature ideas through issues:
- **Title**: Clear feature description
- **Use Case**: Why this feature would be useful
- **Example**: How it would work
- **Alternatives**: Other solutions considered

### 3. Submit Code Changes

#### Setup Development Environment
```bash
# Clone repository
git clone https://github.com/yourusername/bug-tracker.git
cd bug-tracker

# Create feature branch
git checkout -b feature/your-feature-name

# Build and test
mvn clean install
mvn spring-boot:run
```

#### Code Guidelines
- Follow Google Java Style Guide
- Use meaningful variable names
- Add comments for complex logic
- Keep methods focused and small
- Write unit tests for new features

#### Commit Messages
- Use present tense ("Add feature" not "Added feature")
- Be descriptive but concise
- Reference issues when applicable

Example:
```
Add bug export to PDF feature

- Implements PDF generation for bug reports
- Allows filtering bugs before export
- Closes #123
```

#### Testing
```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=BugControllerTest

# Build and check
mvn clean install
```

#### Documentation
- Update README.md if needed
- Add comments to complex code
- Update ARCHITECTURE.md for design changes
- Keep CONFIGURATION.md current

### 4. Pull Request Process

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/my-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "Add feature description"
   ```
4. **Push** to your fork
   ```bash
   git push origin feature/my-feature
   ```
5. **Open** a Pull Request

#### PR Description Template
```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement

## Testing
How was this tested?

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests pass
```

## Development Setup

### Required Tools
- Java 17+
- Maven 3.6+
- Git
- IDE (IntelliJ IDEA or VS Code recommended)

### Build Commands
```bash
# Clean build
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Package JAR
mvn package

# Check code quality
mvn checkstyle:check
```

### IDE Setup

#### IntelliJ IDEA
1. File → Open → Select project folder
2. Configure SDK: File → Project Structure → Project → SDK (select Java 17+)
3. Enable annotation processing: Settings → Build → Compiler → Annotation Processors

#### VS Code
1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Open folder in VS Code

## Project Structure

```
src/main/java/com/example/bugtracker/
├── BugTrackerApplication.java    - Main entry point
├── controller/                   - HTTP handlers
├── service/                      - Business logic
├── repository/                   - Data access
└── model/                        - Entity classes

src/main/resources/
├── application.properties         - Configuration
├── schema.sql                    - Database schema
├── templates/                    - HTML templates
└── static/                       - CSS, JS files
```

## Coding Standards

### Java Code Style
```java
// Good
public class BugService {
    private final BugRepository repository;
    
    public BugService(BugRepository repository) {
        this.repository = repository;
    }
    
    public void createBug(Bug bug) {
        // Validate
        validateBugData(bug);
        
        // Create
        repository.insertBug(bug);
    }
}

// Avoid
public class bugservice {
    BugRepository rep;
    public void c(Bug b) {
        rep.insertBug(b);
    }
}
```

### Comments
```java
// Add comments for WHY, not WHAT
// Good: Why is this conversion needed?
// Convert string date to Date object for database storage
Date detectedDate = dateFormat.parse(dateString);

// Avoid: What does this do? (obvious from code)
// Parse date string
Date detectedDate = dateFormat.parse(dateString);
```

### Error Handling
```java
// Good: Specific exception handling
try {
    connection = getDbConnection();
    // database operations
} catch (SQLException e) {
    logger.error("Database error: {}", e.getMessage());
    throw new DatabaseException("Failed to connect", e);
}

// Avoid: Generic error handling
try {
    // operations
} catch (Exception e) {
    System.out.println("Error: " + e);
}
```

## Testing Requirements

### Unit Tests
- Test controller endpoints
- Test service business logic
- Test repository queries
- Test validation methods

### Test Example
```java
@Test
public void testCreateBug_ValidData_Success() {
    // Arrange
    Bug bug = new Bug();
    bug.setDescription("Test bug");
    bug.setPriority("HIGH");
    
    // Act
    bugService.createBug(bug);
    
    // Assert
    assertTrue(bugRepository.getBugById(bug.getId()) != null);
}

@Test
public void testCreateBug_InvalidData_Failure() {
    // Arrange
    Bug bug = new Bug(); // Empty bug
    
    // Act & Assert
    assertThrows(ValidationException.class, () -> bugService.createBug(bug));
}
```

## Documentation

### README.md
- Project description and purpose
- Quick start guide
- How to run application
- API documentation

### ARCHITECTURE.md
- System design and layers
- Design patterns used
- Component interactions
- Technology choices

### CONFIGURATION.md
- Configuration options
- Environment variables
- Database setup
- Deployment guide

### Code Comments
- Explain complex logic
- Document public methods
- Provide usage examples

## Review Process

All PRs will be reviewed for:
1. **Code Quality**: Follows guidelines
2. **Functionality**: Works as intended
3. **Testing**: Adequate test coverage
4. **Documentation**: Updated as needed
5. **Performance**: No negative impact

## Merge Criteria

A PR can be merged when:
- ✅ All tests pass
- ✅ Code review approved
- ✅ No merge conflicts
- ✅ Documentation updated
- ✅ Commits are clean

## Questions?

- 📖 Read: [README.md](./README.md)
- 📖 Read: [ARCHITECTURE.md](./ARCHITECTURE.md)
- 💬 Ask: Open a discussion or issue
- 📧 Email: your-email@example.com

Thank you for contributing! 🙏
