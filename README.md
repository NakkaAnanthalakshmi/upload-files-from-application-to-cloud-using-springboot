# Cloud Based Certificate Uploader

A secure, modern full-stack application for uploading, viewing, and managing digital certificates in the cloud. Built with Spring Boot for backend APIs, JWT-based authentication, and a React/HTML/CSS frontend designed for ease-of-use and responsive design. All digital certificates are stored in AWS S3 for robust, scalable access and backed by QR code generation for instant verification.

## Features

- Upload certificates securely to cloud storage (AWS S3)
- List, view, and manage certificates for authenticated users
- Download certificates using secure links
- QR code generation for verifying/download access
- User authentication using JWT tokens
- Responsive and attractive UI with gradient styling
- Backend REST API built with Spring Boot
- Modern frontend with HTML, CSS, and JavaScript
- Role-based secured access (admin/user)
- Error handling and informative feedback for all actions

## Technologies Used

- **Java, Spring Boot** (REST backend)
- **AWS S3** (certificate storage)
- **JWT** (authentication)
- **MongoDB** (user and certificate data)
- **React / HTML / CSS / JS** (frontend)
- **QR Code** libraries for JavaScript and Java

## Getting Started

1. Clone the repository:
git clone https://github.com/NakkaAnanthalakshmi/upload-files-from-application-to-cloud-using-springboot.git

2. Backend Setup:
- Configure AWS credentials in environment variables (do **not** commit secrets!)
- Set up MongoDB (local or cloud)
- Run Spring Boot application

3. Frontend Setup:
- Open HTML files with live server or link with React project

## Security Warning

Never commit sensitive secrets (AWS keys, passwords) to the repository. Use environment variables and .gitignore.

## License

This project is licensed under the MIT License.
