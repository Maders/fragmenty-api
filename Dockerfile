FROM openjdk:8

# Set the working directory inside the Docker container
WORKDIR /app

RUN echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian /" | tee /etc/apt/sources.list.d/sbt_old.list && \
    apt-key adv --keyserver "hkp://keyserver.ubuntu.com:80" --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 && \
    apt-get update && \
    apt-get install -y sbt && \
    apt-get clean

# Set the working directory inside the Docker container
WORKDIR /app

# Copy the build.sbt and project/ directory to cache the sbt dependencies
COPY build.sbt .
COPY project project

# Cache the sbt dependencies
# RUN sbt update

# Copy the rest of the application code
COPY . .

# Compile the application
RUN sbt clean compile stage

# Expose the port the app runs on
EXPOSE 9000

# Start the Play application
CMD ["./target/universal/stage/bin/fragmenty-play", "-Dconfig.override_with_env_vars=true"]
