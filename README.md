# Enhanced Country Chatbot

A modern Spring Boot chatbot application that provides information about countries, built with 2025 tech standards.

## 🚀 Features

- **AI-Powered Responses**: Enhanced natural language processing
- **Real-time Data**: Integration with external country APIs
- **Modern UI**: React-based frontend with responsive design
- **Multilingual Support**: i18n for global accessibility
- **Cloud-Ready**: Docker containerization with Kubernetes configs
- **Comprehensive Testing**: Unit, integration, and E2E tests

## 🛠️ Technology Stack

- **Backend**: Spring Boot 3.3.4
- **Frontend**: React 19 with TypeScript
- **API Integration**: RestTemplate with fallback mechanisms
- **Database**: MongoDB for conversation persistence
- **Security**: Spring Security with OAuth2
- **Caching**: Redis for high-performance data retrieval
- **Testing**: JUnit 5, Mockito, React Testing Library
- **CI/CD**: GitHub Actions
- **Containerization**: Docker with Kubernetes

## 📋 Project Structure

```
chatbot/
├── .github/workflows/         # CI/CD pipeline configurations
├── kubernetes/                # K8s deployment manifests
├── src/
│   ├── main/
│   │   ├── java/com/masingita/chatbot/
│   │   │   ├── config/        # Application configurations
│   │   │   ├── controller/    # REST controllers
│   │   │   ├── model/         # Data models
│   │   │   ├── repository/    # Data access layer
│   │   │   ├── service/       # Business logic
│   │   │   └── util/          # Utility classes
│   │   ├── resources/
│   │   │   ├── static/        # Static resources (Legacy UI)
│   │   │   ├── templates/     # Thymeleaf templates (Legacy UI)
│   │   │   └── application.yml # Application properties
│   └── test/                  # Test classes
├── frontend/                  # React application
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── services/
│   │   └── App.tsx
│   ├── package.json
│   └── tsconfig.json
├── Dockerfile                 # Docker configuration
├── docker-compose.yml         # Local development setup
└── pom.xml                    # Maven configuration
```

## ⚙️ Implementation Roadmap

### Phase 1: Core Enhancements
- [x] Add MongoDB for conversation persistence
- [x] Implement Redis caching
- [x] Update error handling and logging
- [x] Add comprehensive test suite

### Phase 2: Modern Frontend
- [x] Create React frontend
- [x] Add TypeScript support
- [x] Implement responsive design
- [x] Add accessibility features

### Phase 3: Advanced Features
- [x] Integrate external country APIs
- [x] Add OAuth2 authentication
- [x] Implement i18n support
- [x] Set up CI/CD pipeline

### Phase 4: Deployment & Monitoring
- [x] Containerize with Docker
- [x] Create Kubernetes configurations
- [x] Set up monitoring with Prometheus
- [x] Implement logging with ELK stack

## 🔄 Key Improvements

### Data Source Enhancement
- Replace static JSON with dynamic API calls to [RestCountries API](https://restcountries.com/)
- Add fallback to local cache for reliability

### Security Upgrades
- CSRF protection
- Rate limiting
- Input sanitization
- OAuth2 authentication

### Performance Optimization
- Redis caching
- Response compression
- Lazy loading in UI

### Testing Strategy
- Unit tests for all services
- Integration tests for APIs
- E2E tests for user flows
- Performance testing

## 🚦 Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 20+
- Docker
- MongoDB
- Redis

### Local Development
```bash
# Clone the repository
git clone https://github.com/masingitaottismaluleke/enhanced-country-chatbot.git

# Start dependencies
docker-compose up -d

# Build and run backend
mvn clean install
mvn spring-boot:run

# Build and run frontend
cd frontend
npm install
npm start
```

### Docker Deployment
```bash
docker build -t enhanced-country-chatbot .
docker run -p 8080:8080 enhanced-country-chatbot
```

### Kubernetes Deployment
```bash
kubectl apply -f kubernetes/
```

## 📄 Documentation

Comprehensive documentation is available in the [docs](./docs) directory, including:
- API documentation
- Architecture diagrams
- User guides
- Development guidelines

## 🧪 Testing

```bash
# Run backend tests
mvn test

# Run frontend tests
cd frontend
npm test

# Run E2E tests
npm run test:e2e
```

## 🔒 Security

This project implements several security best practices:
- Input validation/sanitization
- CSRF protection
- Rate limiting
- Secure headers
- Content Security Policy
- OAuth2 authentication

## 📊 Monitoring

- Prometheus metrics
- Grafana dashboards
- ELK stack for logging

## 👨‍💻 Contact

For inquiries or collaboration:
- **Email**: masingitaottismaluleke@gmail.com
- **Phone**: 0813704024

## 📝 License

This project is licensed under the MIT License