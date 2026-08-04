@echo off
echo ============================================
echo  Iniciando Ambiente Completo (Container)
echo  App + PostgreSQL + Adminer
echo  Microservico: ms-video-upload
echo ============================================

docker-compose up --build -d

echo.
echo    Servicos iniciados:
echo    - API:        http://localhost:8083
echo    - Swagger:    http://localhost:8083/swagger-ui.html
echo    - Actuator:   http://localhost:8083/actuator
echo                  http://localhost:8083/actuator/health
echo                  http://localhost:8083/actuator/health/liveness
echo                  http://localhost:8083/actuator/health/readiness
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
echo    - Localstack:    http://localhost:4568
echo    - sqs-admin:  http://localhost:3998
echo.


