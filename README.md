# Registration Service

Aplicação backend desenvolvida em **Spring Boot** para gerenciamento de registros de usuários, com persistência em **PostgreSQL** e mensageria assíncrona via **Kafka** para envio de notificações.

---

## Como rodar Localmente

### Pré-requisitos
- **Docker** e **Docker Compose** instalados  
- **Java 17+**  
- **Gradle**

### 1. Subir os serviços de infraestrutura
Na raiz do projeto, execute:

```bash
docker-compose up -d
```

Isso irá subir os containers do Postgres, Zookeeper e Kafka.

## 2. Rodar a aplicação Spring Boot

Ainda na raiz do projeto, execute:

```bash
./gradlew bootRun
````
A aplicação ficará disponível em:
http://localhost:8080

## 3. Gerar test report

```bash
./gradlew test jacocoTestReport
``` 
O relatório será gerado em:
build/reports/jacoco/test/html/index.html
