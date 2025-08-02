# CORRECTED CI/CD Pipeline Creation Prompt Template

**COPY THIS EXACT PROMPT - DO NOT MODIFY**

Create a comprehensive GitHub Actions CI/CD pipeline for a Spring Boot microservice with ZERO errors. Follow these EXACT specifications:

## Service-Specific Configuration:
- **Service Directory**: `interviews-service`
- **ECR Repository**: `consultingfirm/interviews-service` 
- **Java Version**: `17` (as specified)
- **SonarQube Project Key**: `sidatKSTX_interviews-microservice`
- **GitHub Organization**: `sidatKSTX`
- **Repository Name**: `interviews-microservice`

## Pipeline Requirements:
1. **Build & Test Job**: Maven build with tests, SonarQube analysis
2. **Build-and-Scan Job**: Docker build, Trivy scans with .trivyignore support, ECR push
3. **Container-Security-Audit Job**: Comprehensive vulnerability assessment with .trivyignore support
4. **Notification Job**: Pipeline completion summary

## CRITICAL FIXES INCLUDED:
- ✅ **ECR Repository**: Full path `consultingfirm/interviews-service` (not just service name)
- ✅ **Container Security Audit**: MUST checkout repository to access .trivyignore file
- ✅ **Comprehensive Trivy Scan**: MUST mount and use .trivyignore file with --ignorefile flag
- ✅ **SonarQube**: Use pom.xml properties for organization, command line for projectKey
- ✅ **Alpine Images**: Use latest eclipse-temurin:17-jre-alpine-3.21 for security fixes
- ✅ **Java Version**: 17 with Zulu distribution

## Pipeline Name and Triggers:
```yaml
name: SonarQube
on:
  push:
    branches:
      - main
  pull_request:
    types: [opened, synchronize, reopened]
  workflow_dispatch:
```

## Environment Variables (EXACT VALUES):
```yaml
env:
  JAVA_VERSION: "17"
  MAVEN_OPTS: "-Xmx1024m"
  ECR_REGISTRY: ${{ secrets.AWS_ACCOUNT_ID }}.dkr.ecr.us-east-1.amazonaws.com
  ECR_REPOSITORY: consultingfirm/interviews-service
  IMAGE_TAG: ${{ github.sha }}
  SERVICE_DIRECTORY: "interviews-service"
  AWS_REGION: "us-east-1"
```

## Required GitHub Secrets:
- `SONAR_TOKEN` - SonarCloud authentication token
- `AWS_ACCOUNT_ID` - AWS account identifier  
- `AWS_ACCESS_KEY_ID` - AWS access credentials
- `AWS_SECRET_ACCESS_KEY` - AWS secret credentials

## Required pom.xml Properties (ADD TO interviews-service/pom.xml):
```xml
<properties>
  <sonar.organization>sidatkstx-1</sonar.organization>
  <sonar.host.url>https://sonarcloud.io</sonar.host.url>
</properties>
```

## SonarQube Build Step (EXACT FORMAT):
```yaml
- name: Build and analyze
  working-directory: ./interviews-service
  env:
    SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
  run: mvn -B verify org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=sidatKSTX_interviews-microservice
```

## Docker Configuration (USE JAVA 17 ALPINE):
```dockerfile
FROM eclipse-temurin:17.0.13_11-jdk-alpine-3.21 AS build
FROM eclipse-temurin:17.0.13_11-jre-alpine-3.21
```

## Container Security Audit Job (CRITICAL FIXES):
```yaml
container-security-audit:
  name: Container Security Audit
  runs-on: ubuntu-latest
  needs: build-and-scan
  if: github.ref == 'refs/heads/main' || github.event_name == 'workflow_dispatch'
  
  steps:
    - name: Checkout repository  # ✅ REQUIRED FOR .trivyignore ACCESS
      uses: actions/checkout@v4
      
    # ... AWS setup steps ...
    
    - name: Run comprehensive Trivy security scan
      run: |
        docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
          -v ${{ github.workspace }}:/workspace \
          -v ${{ github.workspace }}/${{ env.SERVICE_DIRECTORY }}/.trivyignore:/tmp/.trivyignore \
          aquasec/trivy:latest image \
          --ignorefile /tmp/.trivyignore \
          --format json \
          --output /workspace/trivy-comprehensive-results.json \
          "${{ env.IMAGE_URI }}"
```

## Pipeline Structure:
1. **Build Job**: 
   - Checkout with fetch-depth: 0
   - Setup JDK 17 with Zulu distribution
   - Cache SonarQube and Maven packages
   - Build and analyze with sonar.projectKey (organization from pom.xml)

2. **Build-and-Scan Job**:
   - Checkout repository
   - Setup JDK 17
   - Cache Maven packages  
   - Build application
   - Configure AWS credentials
   - Login to ECR
   - Build Docker image with Java 17 Alpine 3.21
   - Run Trivy filesystem scan with SARIF upload
   - Run Trivy image scan with SARIF upload
   - Run Trivy security gate with .trivyignore support
   - Push to ECR with FULL repository path

3. **Container-Security-Audit Job**:
   - **MUST checkout repository first**
   - Configure AWS and pull image from ECR
   - Run comprehensive Trivy scan **WITH .trivyignore support**
   - Analyze vulnerabilities with thresholds
   - Generate security summary

4. **Notification Job**:
   - Aggregate all job results
   - Generate final pipeline summary

## Workflow File Name:
`.github/workflows/ci-interviews-service.yml`

## Pre-Creation Requirements:
1. **ADD to interviews-service/pom.xml**:
   ```xml
   <properties>
     <sonar.organization>sidatkstx-1</sonar.organization>
     <sonar.host.url>https://sonarcloud.io</sonar.host.url>
   </properties>
   ```
2. Create `.trivyignore` file in `interviews-service/` directory
3. Ensure all GitHub secrets are configured
4. Verify ECR repository `consultingfirm/interviews-service` exists
5. Create SonarCloud project with key `sidatKSTX_interviews-microservice`

## .trivyignore Template:
```
# Trivy Ignore File
# Add any vulnerabilities that need to be ignored
# Format: CVE-YYYY-NNNN

# Example for Alpine SQLite vulnerability if needed:
# CVE-2025-6965  # SQLite Integer Truncation - Alpine 3.21 limitation
```

---

**IMPORTANT**: This template includes ALL fixes from the bench-profiles-microservice debugging session. Use EXACTLY as written to avoid the following errors:
- ❌ ECR repository not found (wrong path)
- ❌ .trivyignore file not found (missing checkout)  
- ❌ Comprehensive scan ignoring .trivyignore (missing mount/flag)
- ❌ SonarQube missing organization parameter
- ❌ Security vulnerabilities from old base images

**VALIDATION**: After creation, verify the pipeline handles all these scenarios correctly before first run.