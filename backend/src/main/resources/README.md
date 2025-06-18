# HTTPS Configuration Guide

This guide explains how to set up HTTPS for the BurgerExpress application.

## Development Environment Setup

For development purposes, you can generate a self-signed certificate using the following command:

```bash
keytool -genkeypair -alias tomcat -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650 -storepass changeit
```

When prompted, provide the following information:
- Your name and organizational details
- For "first and last name", you can use "localhost" to avoid certificate warnings in browsers

After generating the keystore, place it in the `src/main/resources` directory.

## Environment Variables

The application uses the following environment variables for SSL configuration:

- `SSL_KEY_STORE`: Path to the keystore file (default: classpath:keystore.p12)
- `SSL_KEY_STORE_PASSWORD`: Password for the keystore (default: changeit)
- `SSL_KEY_STORE_TYPE`: Type of keystore (default: PKCS12)
- `SSL_KEY_ALIAS`: Alias for the key in the keystore (default: tomcat)

You can override these defaults by setting the environment variables in your system or in the `.env` file.

## Production Environment

For production, you should:

1. Obtain a proper SSL certificate from a trusted Certificate Authority
2. Configure the application with the production certificate
3. Ensure all environment variables are properly set
4. Make sure the APP_URL in the .env file points to your production HTTPS URL

### Let's Encrypt Configuration

For production environments, we recommend using Let's Encrypt to obtain free, trusted SSL certificates:

1. Follow the detailed instructions in the `letsencrypt-instructions.txt` file to:
   - Install Certbot
   - Obtain Let's Encrypt certificates
   - Convert certificates to the format required by Spring Boot
   - Configure automatic certificate renewal

Let's Encrypt certificates are valid for 90 days and need to be renewed regularly. The instructions include setting up automatic renewal to ensure your certificates remain valid.

### Production Deployment

For deploying to a production environment with Let's Encrypt:

1. Use the `application-production.properties` file as a template for your production configuration
2. Run the `deploy-with-letsencrypt.sh` script to automate the deployment process:
   ```bash
   sudo ./deploy-with-letsencrypt.sh
   ```

This script will:
- Install necessary dependencies
- Obtain Let's Encrypt certificates
- Configure the application to use these certificates
- Set up automatic certificate renewal
- Create a systemd service for running the application
