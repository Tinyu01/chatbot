# Chatbot Project

This project is a chatbot application built using Java for the backend and React for the frontend. It is designed to handle user interactions and provide responses based on predefined logic.

## Project Structure

The project is organized into several directories, each serving a specific purpose:

- **src/main/java/com/masingita/chatbot/**: Contains the main application code.
  - **config/**: Application configuration classes that manage settings and properties.
  - **controller/**: REST controllers that handle incoming HTTP requests and return responses.
  - **model/**: Data model classes representing the structure of the data used in the application.
  - **repository/**: Repository classes that manage data access and persistence.
  - **service/**: Service classes encapsulating the business logic of the application.
  - **util/**: Utility classes providing helper functions and common operations.

- **src/main/resources/**: Contains resources for the application.
  - **static/**: Static resources such as CSS, JavaScript, and images for the legacy UI.
  - **templates/**: Thymeleaf templates for rendering dynamic HTML content in the legacy UI.
  - **application.yml**: Application properties and configuration settings in YAML format.

- **src/test/**: Contains test classes for unit and integration testing of the application.

- **frontend/**: Contains the React application.
  - **public/**: Public assets for the React application.
  - **src/**: Source code for the React application.
    - **components/**: React component files defining the UI elements.
    - **hooks/**: Custom React hooks for managing state and side effects.
    - **services/**: Service files handling API calls and business logic.
    - **App.tsx**: Main entry point of the React application.

- **Dockerfile**: Instructions for building a Docker image for the application.

- **docker-compose.yml**: Defines services, networks, and volumes for local development using Docker Compose.

- **pom.xml**: Configuration file for Maven, specifying project dependencies and build settings.

## Setup Instructions

1. **Clone the repository**:
   ```
   git clone <repository-url>
   cd chatbot
   ```

2. **Backend Setup**:
   - Navigate to the `src/main/java/com/masingita/chatbot` directory.
   - Use Maven to build the project:
     ```
     mvn clean install
     ```
   - Run the application:
     ```
     mvn spring-boot:run
     ```

3. **Frontend Setup**:
   - Navigate to the `frontend` directory.
   - Install dependencies:
     ```
     npm install
     ```
   - Start the React application:
     ```
     npm start
     ```

## Usage

Once both the backend and frontend are running, you can interact with the chatbot through the web interface. The backend will handle requests and provide responses based on the implemented logic.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any enhancements or bug fixes.

## License

This project is licensed under the MIT License. See the LICENSE file for details.