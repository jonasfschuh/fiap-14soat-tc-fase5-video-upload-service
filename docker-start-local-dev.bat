@echo off
echo ============================================
echo  Iniciando Ambiente de Desenvolvimento
echo  PostgreSQL + Adminer + LocalStack
echo  Microservico: ms-video-upload
echo ============================================
echo.

docker-compose up -d postgres-video-upload adminer localstack

echo.
echo   Servicos iniciados:
echo    - PostgreSQL: localhost:5433
echo            Host: localhost:5433
echo            Database: video_upload_db
echo            Username: postgres
echo            Password: postgres
echo    - Adminer:    http://localhost:8093
echo            Sistema: PostgreSQL
echo            Servidor: postgres-video-upload (ou localhost se acessar de fora)
echo            Usuario: postgres
echo            Senha: postgres
echo            Base de dados: video_upload_db
echo    - LocalStack: http://localhost:4566
echo    - StackPort:  http://localhost:8080
echo.
echo  Execute a aplicacao no IntelliJ com as seguintes variaveis de ambiente:
echo    AWS_SQS_ENABLED=true
echo    AWS_SQS_ENDPOINT=http://localhost:4566
echo    SPRING_PROFILES_ACTIVE=dev
echo.
echo  Porta local da API: 8083
echo  http://localhost:8083/swagger-ui.html
echo.
