# Healthcare Data Integration & Compliance Gateway

## 1. Quick Summary
**Project Name:** Healthcare Data Integration & Compliance Gateway
**Architecture:** Microservices
**Tech Stack:** Java 17, Spring Boot 3.2, Spring Cloud (Gateway, Eureka, Config), Maven.

This project is a robust, scalable microservices-based platform designed to facilitate the secure integration, normalization, and auditing of healthcare data. It acts as a central gateway that bridges modern healthcare applications with legacy systems, ensuring data interoperability and strict compliance with regulatory standards (like HIPAA). The system leverages **Spring Cloud** for distributed system patterns, including service discovery, centralized configuration, and intelligent routing. Key features include a **Normalization Service** for data transformation, an **Audit Log Service** for compliance tracking, and a **Legacy Mock Service** to simulate and test integrations with older healthcare infrastructure.

## 2. Key Highlights
*   Architected scalable microservices system for healthcare data integration using Spring Boot.
*   Implemented centralized API Gateway for secure routing, rate limiting, and load balancing.
*   Designed Service Discovery mechanism with Netflix Eureka for dynamic service scaling.
*   Established Audit Log Service to ensure HIPAA compliance and immutable traceability.
*   Developed Normalization Service to transform legacy healthcare data into standard formats.
*   Integrated centralized configuration management using Spring Cloud Config Server.
*   Simulated legacy healthcare systems for robust integration testing and validation.
*   Orchestrated secure inter-service communication and data flow across distributed components.

## 3. In-Depth Analysis & Flow Breakdown

### System Architecture
The system follows a standard **Microservices Architecture**, where each distinct functional capability is encapsulated in its own deployable unit. These services communicate over HTTP/REST, coordinated by the Spring Cloud ecosystem.

```mermaid
graph TD
    Client[Client App / External System]
    Gateway[API Gateway<br/>(Spring Cloud Gateway)]
    Discovery[Discovery Service<br/>(Eureka Server)]
    Config[Config Server]
    
    subgraph "Core Services"
        Auth[Auth Service]
        Norm[Normalization Service]
        Audit[Audit Log Service]
    end
    
    subgraph "External/Legacy Simulation"
        Legacy[Legacy Mock Service]
    end

    Client -->|HTTPS| Gateway
    Gateway -.->|Register/Lookup| Discovery
    Auth -.->|Register| Discovery
    Norm -.->|Register| Discovery
    Audit -.->|Register| Discovery
    Legacy -.->|Register| Discovery
    
    Gateway -->|Route Request| Auth
    Gateway -->|Route Request| Norm
    Gateway -->|Route Request| Audit
    
    Norm -->|Fetch Data| Legacy
    Norm -->|Log Event| Audit
    Auth -->|Log Event| Audit
    
    Services -->|Fetch Config| Config
```

### Component Breakdown

1.  **API Gateway (`api-gateway`)**
    *   **Role:** The single entry point for all client requests.
    *   **Functionality:** It handles routing, load balancing, and security (SSL termination). It queries the **Discovery Service** to find the location of downstream services, abstracting the physical network topology from the client.
    *   **Tech:** Spring Cloud Gateway.

2.  **Discovery Service (`discovery-service`)**
    *   **Role:** The service registry (Phonebook).
    *   **Functionality:** All microservices register themselves here upon startup. The Gateway and other services consult Eureka to find the IP addresses and ports of the services they need to call. This enables dynamic scaling (adding/removing instances without config changes).
    *   **Tech:** Netflix Eureka Server.

3.  **Config Server (`config-server`)**
    *   **Role:** Centralized configuration management.
    *   **Functionality:** Stores and serves configuration properties (database URLs, feature flags) for all services. This allows configuration changes without redeploying services.

4.  **Normalization Service (`normalization-service`)**
    *   **Role:** The core business logic for data integration.
    *   **Functionality:** It accepts requests for data, fetches raw data from the **Legacy Mock Service**, and transforms it into a canonical/standard format (e.g., converting proprietary legacy formats to FHIR-compliant JSON).

5.  **Legacy Mock Service (`legacy-mock-service`)**
    *   **Role:** Simulation of external dependencies.
    *   **Functionality:** Mimics the behavior of an older Hospital Information System (HIS) or Electronic Health Record (EHR). It provides endpoints that return data in "legacy" formats, allowing the Normalization Service to be tested and developed without a real connection to sensitive production systems.

6.  **Audit Log Service (`audit-log-service`)**
    *   **Role:** Compliance and Security.
    *   **Functionality:** Listens for or receives audit events from other services. Every access to patient data, login attempt, or data transformation is logged here. This is critical for meeting healthcare regulations (HIPAA/GDPR).

7.  **Auth Service (`auth-service`)**
    *   **Role:** Identity and Access Management.
    *   **Functionality:** Handles user authentication (login) and issues tokens (likely JWT). The Gateway can use this service to validate requests before routing them to business services.

### Detailed Data Flow (Example: Fetching Patient Data)

1.  **Client Request:** A client (e.g., a doctor's dashboard) sends a request to `GET /api/normalization/patient/{id}` via the **API Gateway**.
2.  **Routing:** The Gateway looks up `normalization-service` in the **Discovery Service** and forwards the request.
3.  **Processing (Normalization):**
    *   The **Normalization Service** receives the request.
    *   It determines it needs data from the legacy system.
    *   It calls the **Legacy Mock Service** (e.g., `GET /legacy/patient/{id}`).
4.  **Data Retrieval:** The **Legacy Mock Service** returns raw, unstructured, or proprietary data.
5.  **Transformation:** The **Normalization Service** maps the raw data to a standardized model.
6.  **Auditing:** The **Normalization Service** sends an asynchronous event to the **Audit Log Service**: *"User X accessed Patient Y data at Time Z"*.
7.  **Response:** The standardized data is returned to the Gateway, which sends it back to the Client.

This architecture ensures that the legacy system is decoupled from the modern frontend, data is always accessed securely and consistently, and every action is traceable.
