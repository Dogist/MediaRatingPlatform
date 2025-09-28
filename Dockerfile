FROM library/postgres:17.6
VOLUME /var/lib/postgresql/data
ENV POSTGRES_USER docker
ENV POSTGRES_PASSWORD docker
ENV POSTGRES_DB docker

COPY init.sql /docker-entrypoint-initdb.d/
