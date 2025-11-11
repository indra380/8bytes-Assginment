# Jenkins Pipelines - End-to-End Application CI/CD

This directory contains Jenkins pipeline definitions for implementing a complete Continuous Integration and Continuous Deployment (CI/CD) workflow for modern applications.

## 📋 Overview

The Jenkins pipeline automates the entire software delivery lifecycle from code checkout to production deployment. It includes:
- **Source Control Integration**: Automatic triggering on code commits
- **Build Automation**: Compilation and packaging with Maven
- **Testing**: Unit and integration tests with automated reporting
- **Security Scanning**: Dependency vulnerability scanning
- **Containerization**: Docker image building and publishing
- **Notifications**: Slack and email alerts

## 📁 File Structure

```
./
├── end-to-end-pipeline.groovy    # Main Jenkins pipeline definition
└── README.md                      # This file
```

## 🔍 Pipeline Overview

The pipeline is designed using Jenkins Declarative Pipeline syntax and follows these stages:

### Stage 1: Checkout
- Clones the repository from GitHub
- Handles both pull requests and main branch commits
- Generates a short Git SHA for versioning

### Stage 2: Build
- Cleans previous build artifacts
- Compiles application code
- Packages application (typically JAR/WAR for Java)
- Uses Maven as build tool

### Stage 3: Unit Tests
- Executes unit tests using Maven
- Publishes test results in JUnit format
- Fails pipeline if tests fail

### Stage 4: Integration Tests
- Runs integration tests (main branch only, not on PRs)
- Uses Maven with integration-tests profile
- Publishes integration test results

### Stage 5: Dependency Vulnerability Scan
- Scans project dependencies for known vulnerabilities
- Uses Maven Dependency-Check plugin
- Publishes vulnerability report
- Runs on main branch only

### Stage 6: Docker Build & Push
- Builds Docker image with versioned tag
- Pushes image to Docker Registry
- Creates both versioned and latest tags
- Triggers on main branch (excludes PRs)

## 🚀 Getting Started

### Prerequisites

#### Jenkins Server
- Jenkins 2.300+ installed and running
- Blue Ocean plugin (recommended for better UI)

#### Tools & Plugins Required
- **JDK 25**: Java Development Kit
- **Maven 3.6.3+**: Build tool
- **Docker**: For containerization
- **Git**: Version control integration

#### Jenkins Plugins
```
- Docker Pipeline
- Email Extension Plugin
- Slack Notification Plugin
- JUnit Plugin
- Dependency-Check Plugin
- Pipeline plugin
```

#### Environment Setup
Create Jenkins credentials:
- `docker-hub-credentials`: Docker registry authentication (username + password)
- Configure Git repository access
- Set up Slack webhook (optional)
- Configure email settings (optional)

### Configuration Steps

1. **Create Jenkins Job**:
   - Create a new Pipeline job
   - Name it appropriately (e.g., "application-pipeline")
   - Configure pipeline script from SCM

2. **Configure Pipeline Script**:
   - Set pipeline definition to "Pipeline script from SCM"
   - Repository URL: `https://github.com/repo/project.git`
   - Script path: `Jenkins Pipelines end-to-end application CI&CD/end-to-end-pipeline.groovy`

3. **Configure Build Triggers**:
   - Enable "GitHub hook trigger for GITScm polling"
   - Or use "Poll SCM" with cron schedule

4. **Update Environment Variables**:
   Edit `end-to-end-pipeline.groovy` and update:
   ```groovy
   DOCKER_REGISTRY = "your-docker-registry"
   DOCKER_REPO = "your-docker-repo"
   PROJECT_NAME = "your-project-name"
   SLACK_CHANNEL = "#your-slack-channel"
   EMAIL_TO = "your-email@example.com"
   ```

## 📊 Pipeline Configuration Details

### Tools Configuration
```groovy
tools {
    jdk 'JDK-25'              # Java Development Kit
    maven 'Maven-3.6.3'       # Maven build tool
}
```

### Environment Variables
| Variable | Purpose | Example |
|----------|---------|---------|
| `DOCKER_REGISTRY` | Docker registry URL | `docker.io` |
| `DOCKER_REPO` | Repository in registry | `mycompany` |
| `IMAGE_TAG` | Docker image tag | `latest` or versioned |
| `PROJECT_NAME` | Application name | `my-app` |
| `SLACK_CHANNEL` | Slack notification channel | `#devops` |
| `EMAIL_TO` | Email recipient | `dev-team@company.com` |
| `IS_PR` | Pull request identifier | Detected automatically |

### Pipeline Options
- **buildDiscarder**: Keeps last 30 builds (saves disk space)
- **timestamps**: Adds timestamps to console output
- **ansiColor**: Enables colored console output
- **disableConcurrentBuilds**: Prevents parallel execution

## 🔄 Pipeline Workflow

```
Commit to main
    ↓
[Checkout] Clone repository, generate image tag
    ↓
[Build] Maven clean package
    ↓
[Unit Tests] mvn test + JUnit reporting
    ↓
[Integration Tests] mvn verify (main only)
    ↓
[Dependency Scan] Check for vulnerabilities (main only)
    ↓
[Docker Build & Push] Build and push image (main only)
    ↓
[Notifications] Slack + Email alerts
```

## 🔐 Security Features

1. **Dependency Scanning**: Identifies vulnerable dependencies before deployment
2. **Test Coverage**: Unit and integration tests ensure code quality
3. **Docker Registry Authentication**: Secure credential management
4. **Main Branch Protection**: Critical stages only run on main branch
5. **PR Isolation**: Pull requests skip deployment stages

## 📤 Artifacts

Pipeline generates:
- **Build Artifacts**: JAR/WAR files (target directory)
- **Test Reports**: JUnit XML reports
- **Dependency Report**: Vulnerability scan results
- **Docker Image**: Container image in registry

## 🔔 Notifications

### Slack Integration
- Sends pipeline status updates
- Posts to configured channel
- Requires Slack webhook configuration

### Email Integration
- Sends build failure notifications
- Includes build logs and artifacts
- Configurable recipients

### Post Actions
```groovy
post {
    always {
        // Cleanup and report
    }
    success {
        // Success notifications
    }
    failure {
        // Failure alerts
    }
}
```

## 🛠️ Troubleshooting

### Common Issues

**1. Maven Build Fails**
```bash
# Check Maven configuration
mvn --version
# Verify pom.xml
mvn validate
```

**2. Docker Push Fails**
- Verify Docker credentials in Jenkins
- Check Docker registry URL
- Confirm registry access permissions

**3. Tests Fail**
- Review test output in Jenkins logs
- Run tests locally: `mvn test`
- Check test dependencies

**4. Slack/Email Notifications Don't Work**
- Verify credentials configuration
- Test webhook connectivity
- Check SMTP settings for email

## 📚 Best Practices

1. **Code Quality**: Maintain test coverage > 80%
2. **Security**: Regular dependency updates
3. **Versioning**: Use semantic versioning for releases
4. **Documentation**: Keep code well-commented
5. **Monitoring**: Set up alerts for pipeline failures
6. **Backup**: Maintain Jenkins configuration backups

## 🔄 Extending the Pipeline

### Adding New Stages

```groovy
stage('Stage Name') {
    when {
        expression { return condition }
    }
    steps {
        sh 'your-command-here'
    }
    post {
        always {
            // Cleanup
        }
    }
}
```

### Adding SonarQube Analysis

```groovy
stage('Code Quality') {
    steps {
        sh 'mvn sonar:sonar'
    }
}
```

### Adding Deployment Stage

```groovy
stage('Deploy') {
    when {
        branch 'main'
    }
    steps {
        sh './deploy.sh'
    }
}
```

## 📖 Documentation Links

- [Jenkins Pipeline Documentation](https://www.jenkins.io/doc/book/pipeline/)
- [Groovy Syntax Guide](https://groovy-lang.org/syntax.html)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Docker Documentation](https://docs.docker.com/)

## 🎯 Next Steps

1. Configure Jenkins server with required plugins
2. Create Jenkins credentials for Docker registry
3. Set up GitHub webhook for automatic triggering
4. Customize environment variables for your project
5. Test pipeline with a commit to main branch
6. Monitor pipeline execution and address any issues

## 📝 Maintenance

- **Weekly**: Monitor pipeline logs for errors
- **Monthly**: Update tool versions (Maven, JDK)
- **Quarterly**: Review and optimize pipeline stages
- **Annually**: Update dependencies and security patches

## 📄 License

This Jenkins pipeline configuration is provided as-is for CI/CD automation purposes.

## ✏️ Contributing

When modifying the pipeline:
- Test changes in a feature branch first
- Document pipeline modifications
- Update this README with new stages
- Follow Jenkins best practices
- Request review from DevOps team before merge
