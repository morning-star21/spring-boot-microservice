# Spring Boot Microservices with JWT Authentication - Single Tomcat Deployment

This project demonstrates how to deploy a microservices architecture using Spring Boot and JWT authentication to a single Tomcat instance on an EC2 server. The services are packaged as WAR files and deployed to Tomcat running on port 8080.

## Project Structure

- **auth-service**: Authentication service that handles JWT tokens and user authentication
- **user-service**: User management service for user profile operations
- **api-gateway**: Gateway that routes requests to the appropriate microservices
- **eureka-server**: Service discovery server
- **common-lib**: Shared code library for all services

## Prerequisites

- JDK 17 or higher
- Maven 3.8+
- PostgreSQL database
- Tomcat 10.x installed on EC2 instance
- EC2 instance with at least 2GB RAM

## Step-by-Step Deployment Instructions

### 1. Set up the EC2 instance

1. Launch an EC2 instance with Amazon Linux 2 or Ubuntu
2. Install Java:
   \`\`\`bash
   # For Amazon Linux 2
   sudo amazon-linux-extras install java-openjdk17
   
   # For Ubuntu
   sudo apt update
   sudo apt install openjdk-17-jdk
   \`\`\`
   
3. Install Tomcat 10:
   \`\`\`bash
   # Create directory
   sudo mkdir -p /opt/tomcat
   cd /opt/tomcat
   
   # Download Tomcat
   sudo wget https://downloads.apache.org/tomcat/tomcat-10/v10.1.15/bin/apache-tomcat-10.1.15.tar.gz
   sudo tar -xvzf apache-tomcat-10.1.15.tar.gz
   
   # Set environment variables
   echo "export CATALINA_HOME=/opt/tomcat/apache-tomcat-10.1.15" >> ~/.bashrc
   source ~/.bashrc
   
   # Set permissions
   sudo chmod +x $CATALINA_HOME/bin/*.sh
   \`\`\`

4. Configure Tomcat user roles:
   \`\`\`bash
   sudo nano $CATALINA_HOME/conf/tomcat-users.xml
   \`\`\`
   
   Add the following inside the `<tomcat-users>` tag:
   \`\`\`xml
   <role rolename="manager-gui"/>
   <role rolename="manager-script"/>
   <user username="admin" password="your_secure_password" roles="manager-gui,manager-script"/>
   \`\`\`

5. Install PostgreSQL:
   \`\`\`bash
   # For Amazon Linux 2
   sudo amazon-linux-extras install postgresql13
   
   # For Ubuntu
   sudo apt install postgresql postgresql-contrib
   \`\`\`

6. Start PostgreSQL and create databases:
   \`\`\`bash
   # Start service
   sudo systemctl start postgresql
   sudo systemctl enable postgresql
   
   # Create databases
   sudo -u postgres psql -c "CREATE DATABASE authdb;"
   sudo -u postgres psql -c "CREATE DATABASE userdb;"
   sudo -u postgres psql -c "CREATE USER appuser WITH ENCRYPTED PASSWORD 'apppassword';"
   sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE authdb TO appuser;"
   sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE userdb TO appuser;"
   \`\`\`

7. Start Tomcat:
   \`\`\`bash
   $CATALINA_HOME/bin/startup.sh
   \`\`\`

8. Configure Tomcat to allow access to the manager:
   \`\`\`bash
   sudo nano $CATALINA_HOME/webapps/manager/META-INF/context.xml
   \`\`\`
   
   Comment out the Valve definition:
   \`\`\`xml
   <!-- <Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" /> -->
   \`\`\`

9. Restart Tomcat:
   \`\`\`bash
   $CATALINA_HOME/bin/shutdown.sh
   $CATALINA_HOME/bin/startup.sh
   \`\`\`

### 2. Configure Security Groups

Ensure your EC2 security group allows:
- SSH (port 22)
- HTTP (port 80)
- HTTPS (port 443)
- Tomcat (port 8080)

### 3. Build the WAR files

1. Clone the repository:
   \`\`\`bash
   git clone https://github.com/your-repo/spring-microservices.git
   cd spring-microservices
   \`\`\`

2. Build the project:
   \`\`\`bash
   mvn clean package
   \`\`\`

3. The WAR files will be created in the following locations:
   - `auth-service/target/auth-service.war`
   - `user-service/target/user-service.war`
   - `api-gateway/target/ROOT.war` (Note: This is the root application)
   - `eureka-server/target/eureka-server.war`

### 4. Deploy to Tomcat

#### Option 1: Using the Tomcat Manager Web UI

1. Access the Tomcat Manager at `http://<your-ec2-ip>:8080/manager/html`
2. Enter the credentials you configured earlier (admin/your_secure_password)
3. In the "WAR file to deploy" section, select your WAR files and click "Deploy"
   - Deploy `auth-service.war` 
   - Deploy `user-service.war`
   - Deploy `eureka-server.war`
   - Deploy `ROOT.war` (API Gateway)
4. After deployment, you should see all applications listed in the manager

#### Option 2: Using the Tomcat Manager API

1. Copy the WAR files to the EC2 instance:
   \`\`\`bash
   scp -i path/to/your-key.pem auth-service/target/auth-service.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem user-service/target/user-service.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem eureka-server/target/eureka-server.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem api-gateway/target/ROOT.war ec2-user@your-ec2-ip:~
   \`\`\`

2. Deploy using the Tomcat Manager API:
   \`\`\`bash
   # For auth-service
   curl -v -u admin:your_secure_password -T auth-service.war "http://localhost:8080/manager/text/deploy?path=/auth-service&update=true"
   
   # For user-service
   curl -v -u admin:your_secure_password -T user-service.war "http://localhost:8080/manager/text/deploy?path=/user-service&update=true"
   
   # For eureka-server
   curl -v -u admin:your_secure_password -T eureka-server.war "http://localhost:8080/manager/text/deploy?path=/eureka-server&update=true"
   
   # For API Gateway (ROOT application)
   curl -v -u admin:your_secure_password -T ROOT.war "http://localhost:8080/manager/text/deploy?path=/&update=true"
   \`\`\`

#### Option 3: Manual Deployment

1. Copy the WAR files to the EC2 instance:
   \`\`\`bash
   scp -i path/to/your-key.pem auth-service/target/auth-service.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem user-service/target/user-service.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem eureka-server/target/eureka-server.war ec2-user@your-ec2-ip:~
   scp -i path/to/your-key.pem api-gateway/target/ROOT.war ec2-user@your-ec2-ip:~
   \`\`\`

2. Stop Tomcat:
   \`\`\`bash
   $CATALINA_HOME/bin/shutdown.sh
   \`\`\`

3. Copy the WAR files to the Tomcat webapps directory:
   \`\`\`bash
   sudo cp ~/auth-service.war $CATALINA_HOME/webapps/
   sudo cp ~/user-service.war $CATALINA_HOME/webapps/
   sudo cp ~/eureka-server.war $CATALINA_HOME/webapps/
   sudo cp ~/ROOT.war $CATALINA_HOME/webapps/
   \`\`\`

4. Start Tomcat:
   \`\`\`bash
   $CATALINA_HOME/bin/startup.sh
   \`\`\`

### 5. Configure Environment Variables

Create a setenv.sh file to set environment variables for Tomcat:

\`\`\`bash
sudo nano $CATALINA_HOME/bin/setenv.sh
\`\`\`

Add the following content:

\`\`\`bash
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_PORT=5432
export DB_USERNAME=appuser
export DB_PASSWORD=apppassword
export AUTH_DB_NAME=authdb
export USER_DB_NAME=userdb
export JWT_SECRET=your_very_secure_jwt_secret_key_at_least_32_characters
\`\`\`

Make the file executable:

\`\`\`bash
sudo chmod +x $CATALINA_HOME/bin/setenv.sh
\`\`\`

Restart Tomcat:

\`\`\`bash
$CATALINA_HOME/bin/shutdown.sh
sleep 5
$CATALINA_HOME/bin/startup.sh
\`\`\`

### 6. Verify the Deployment

1. Check the logs:
   \`\`\`bash
   tail -f $CATALINA_HOME/logs/catalina.out
   \`\`\`

2. Access the services:
   - API Gateway Status: `http://<your-ec2-ip>:8080/status`
   - Eureka Server: `http://<your-ec2-ip>:8080/eureka-server`
   - Auth Service Status: `http://<your-ec2-ip>:8080/auth-service/api/auth/status`
   - User Service Status: `http://<your-ec2-ip>:8080/user-service/api/users/status`
   
3. Check the Swagger documentation:
   - Auth Service API Docs: `http://<your-ec2-ip>:8080/auth-service/swagger-ui.html`
   - User Service API Docs: `http://<your-ec2-ip>:8080/user-service/swagger-ui.html`

## Testing the Services

### 1. Register a new user

\`\`\`bash
curl -X POST http://<your-ec2-ip>:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123","role":["user"]}'
\`\`\`

### 2. Authenticate and get JWT token

\`\`\`bash
curl -X POST http://<your-ec2-ip>:8080/api/auth/signin \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
\`\`\`

Save the access token from the response.

### 3. Access the protected user endpoint

\`\`\`bash
curl -X GET http://<your-ec2-ip>:8080/api/users/username/testuser \
  -H "Authorization: Bearer <your-jwt-token>"
\`\`\`

## Troubleshooting

### Common Issues

1. **Services not starting**: Check the Tomcat logs for error messages
   \`\`\`bash
   tail -f $CATALINA_HOME/logs/catalina.out
   \`\`\`

2. **Database connection issues**: Verify PostgreSQL is running and accessible
   \`\`\`bash
   sudo systemctl status postgresql
   \`\`\`

3. **Permission problems**: Ensure Tomcat has proper file permissions
   \`\`\`bash
   sudo chown -R tomcat:tomcat $CATALINA_HOME
   \`\`\`

4. **Out of memory errors**: Increase Tomcat's memory allocation
   \`\`\`bash
   echo 'CATALINA_OPTS="$CATALINA_OPTS -Xms512m -Xmx1024m"' >> $CATALINA_HOME/bin/setenv.sh
   \`\`\`

5. **Service-to-service communication failing**: Check if the services can communicate with each other through Eureka

### Security Considerations

1. Use HTTPS in production by configuring SSL in Tomcat
2. Secure the JWT secret key
3. Restrict access to the Tomcat manager
4. Configure proper database user permissions
5. Implement rate limiting for API endpoints

## Backup and Maintenance

### Database Backup

Set up regular PostgreSQL backups:

\`\`\`bash
# Create a backup script
sudo nano /usr/local/bin/backup_dbs.sh
\`\`\`

Add the following content:

\`\`\`bash
#!/bin/bash
BACKUP_DIR="/opt/backups"
DATE=$(date +"%Y%m%d%H%M")

mkdir -p $BACKUP_DIR

# Backup authdb
pg_dump -U postgres -Fc authdb > $BACKUP_DIR/authdb_$DATE.dump

# Backup userdb
pg_dump -U postgres -Fc userdb > $BACKUP_DIR/userdb_$DATE.dump
\`\`\`

Make it executable and set up a cron job:

\`\`\`bash
sudo chmod +x /usr/local/bin/backup_dbs.sh

# Add to crontab (run daily at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /usr/local/bin/backup_dbs.sh") | crontab -
\`\`\`

### Log Rotation

Tomcat log rotation can be configured in `$CATALINA_HOME/conf/server.xml`.

## Conclusion

You have now successfully deployed the Spring Boot microservices to a single Tomcat instance running on EC2. The API Gateway serves as the entry point for all requests, providing a clean URL structure without exposing implementation details.

For production environments, consider implementing additional security measures and setting up monitoring for the services.
