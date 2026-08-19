@echo off
echo ============================================
echo  Iniciando Ambiente de Desenvolvimento
echo  Adminer (UI para o PostgreSQL do K8s)
echo  Microservico: ms-video-upload
echo ============================================
echo.
echo  PRE-REQUISITO: infraestrutura do iac-terraform rodando.
echo  Os recursos abaixo sao provisionados pelo repositorio:
echo    fiap-14soat-tc-fase5-iac-terraform
echo.

docker-compose up -d adminer

echo.
echo   Servicos iniciados:
echo    - Adminer:    http://localhost:8093
echo            Sistema: PostgreSQL
echo            Servidor: host.docker.internal:5433
echo            Usuario: postgres
echo            Senha: postgres
echo            Base de dados: video_upload_db
echo.
echo   Recursos compartilhados (iac-terraform):
echo    - PostgreSQL: localhost:5433   (banco: video_upload_db)
echo    - RabbitMQ:   localhost:5672   (vhost: fiapx / user: fiapx / pass: fiapx123)
echo    - RabbitMQ UI: http://localhost:15672
echo.
echo  Execute a aplicacao no IntelliJ com as seguintes variaveis de ambiente:
echo    SPRING_PROFILES_ACTIVE=dev
echo    SERVER_PORT=8083
echo    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/video_upload_db
echo    SPRING_DATASOURCE_USERNAME=postgres
echo    SPRING_DATASOURCE_PASSWORD=postgres
echo    STORAGE_TYPE=local
echo    STORAGE_LOCAL_PATH=./uploads
echo    MAX_UPLOAD_SIZE=500MB
echo    RABBITMQ_HOST=localhost
echo    RABBITMQ_PORT=5672
echo    RABBITMQ_VHOST=fiapx
echo    RABBITMQ_USER=fiapx
echo    RABBITMQ_PASSWORD=fiapx123
echo    AUTH_SERVICE_URL=http://localhost:8090
echo.
echo  Porta local da API: 8083
echo  http://localhost:8083/swagger-ui.html
echo.
